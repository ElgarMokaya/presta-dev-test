package com.elgar.walletsystem.service.serviceImpl;

import com.elgar.walletsystem.model.ReconciliationFile;
import com.elgar.walletsystem.model.ReconciliationItem;
import com.elgar.walletsystem.model.ReconciliationSummary;
import com.elgar.walletsystem.enums.FileSource;
import com.elgar.walletsystem.enums.ReconciliationStatus;
import com.elgar.walletsystem.dto.response.PagedResponse;
import com.elgar.walletsystem.dto.response.ReconciliationItemResponse;
import com.elgar.walletsystem.dto.response.ReconciliationSummaryResponse;
import com.elgar.walletsystem.mapping.ReconciliationMapper;
import com.elgar.walletsystem.model.WalletTransaction;
import com.elgar.walletsystem.repository.ReconciliationFileRepository;
import com.elgar.walletsystem.repository.ReconciliationItemRepository;
import com.elgar.walletsystem.repository.ReconciliationSummaryRepository;
import com.elgar.walletsystem.repository.WalletTransactionRepository;
import com.elgar.walletsystem.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReconciliationServiceImpl implements ReconciliationService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final ReconciliationFileRepository fileRepository;
    private final ReconciliationItemRepository itemRepository;
    private final ReconciliationSummaryRepository summaryRepository;
    private final ReconciliationMapper mapper;

@Override
@Transactional
public void ingestFile(LocalDate businessDate, String source, MultipartFile file) {
    // Save metadata
    ReconciliationFile reconFile = new ReconciliationFile();
    reconFile.setBusinessDate(businessDate);
    reconFile.setFileName(file.getOriginalFilename());
    reconFile.setFileSource(FileSource.valueOf(source.toUpperCase()));
    reconFile.setIngestedAt(new Date().toInstant());
    fileRepository.save(reconFile);

    try (var reader = new InputStreamReader(file.getInputStream())) {
        Scanner scanner = new Scanner(reader);

        // Skip the header row
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split(",");

            // Validate that the line has at least 2 parts (for txnId and amount)
            if (parts.length < 2) {
                continue;
            }

            try {
                String txnId = parts[0].trim();
                BigDecimal amount = new BigDecimal(parts[1].trim());

                // Store external reference
                ReconciliationItem item = new ReconciliationItem();
                item.setBusinessDate(businessDate);
                item.setTransactionId(txnId);
                item.setExternalAmount(amount);
                item.setStatus(ReconciliationStatus.MISSING_INTERNAL);
                itemRepository.save(item);
            } catch (NumberFormatException e) {
                System.err.println("Skipping line due to invalid amount format: " + line);
            }
        }
    } catch (Exception e) {
        throw new RuntimeException("Failed to parse reconciliation file", e);
    }
}

    @Override
    @Transactional
    public ReconciliationSummaryResponse generateReport(LocalDate businessDate) {
        // Get internal transactions
        List<WalletTransaction> internalTxns =
    walletTransactionRepository.findAll().stream()
        .filter(txn -> txn.getCreatedAt()
                          .atZone(ZoneId.of("UTC"))   // convert Instant to ZonedDateTime
                          .toLocalDate()              // extract LocalDate
                          .equals(businessDate))
        .collect(Collectors.toList());


        // Get external items
        List<ReconciliationItem> externalItems =
                itemRepository.findByBusinessDate(businessDate, Pageable.unpaged()).getContent();

        Map<String, WalletTransaction> internalMap = internalTxns.stream()
                .collect(Collectors.toMap(WalletTransaction::getClientTxnId, t -> t));

        Map<String, ReconciliationItem> externalMap = externalItems.stream()
                .collect(Collectors.toMap(ReconciliationItem::getTransactionId, i -> i));

        // Compare sets
        int matched = 0, missingInternal = 0, missingExternal = 0, amountMismatch = 0;
        BigDecimal netDiff = BigDecimal.ZERO;

        for (var entry : internalMap.entrySet()) {
            String txnId = entry.getKey();
            WalletTransaction txn = entry.getValue();
            ReconciliationItem external = externalMap.get(txnId);

            if (external == null) {
                // Exists internally but missing externally
                ReconciliationItem item = new ReconciliationItem();
                item.setBusinessDate(businessDate);
                item.setTransactionId(txnId);
                item.setInternalAmount(txn.getAmount());
                item.setStatus(ReconciliationStatus.MISSING_INTERNAL);
                itemRepository.save(item);
                missingExternal++;
                netDiff = netDiff.add(txn.getAmount());
            } else {
                if (txn.getAmount().compareTo(external.getExternalAmount()) == 0) {
                    external.setInternalAmount(txn.getAmount());
                    external.setStatus(ReconciliationStatus.MATCHED);
                    matched++;
                } else {
                    external.setInternalAmount(txn.getAmount());
                    external.setStatus(ReconciliationStatus.AMOUNT_MISMATCH);
                    amountMismatch++;
                    netDiff = netDiff.add(txn.getAmount().subtract(external.getExternalAmount()));
                }
                itemRepository.save(external);
            }
        }

        for (var entry : externalMap.entrySet()) {
            if (!internalMap.containsKey(entry.getKey())) {
                missingInternal++;
            }
        }

        // Save summary
        ReconciliationSummary summary = new ReconciliationSummary();
        summary.setBusinessDate(businessDate);
        summary.setMatchedCount(matched);
        summary.setMissingInternal(missingInternal);
        summary.setMissingExternal(missingExternal);
        summary.setAmountMismatch(amountMismatch);
        summary.setNetDifference(netDiff);
        summaryRepository.save(summary);

        return mapper.toSummaryResponse(summary);
    }

    @Override
    public PagedResponse<ReconciliationItemResponse> listItems(LocalDate businessDate, Pageable pageable) {
        Page<ReconciliationItem> page = itemRepository.findByBusinessDate(businessDate, pageable);
        return PagedResponse.<ReconciliationItemResponse>builder()
                .content(page.getContent().stream()
                        .map(mapper::toItemResponse)
                        .collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public byte[] exportCsv(LocalDate businessDate) {
        List<ReconciliationItem> items = itemRepository.findByBusinessDate(businessDate, Pageable.unpaged()).getContent();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos);
             CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("transaction_id","internal_amount","external_amount","status"))) {

            for (ReconciliationItem item : items) {
                csv.printRecord(item.getTransactionId(), item.getInternalAmount(), item.getExternalAmount(), item.getStatus());
            }
            csv.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV", e);
        }
    }
}
