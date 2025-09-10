
ALTER TABLE wallet_transaction
ADD CONSTRAINT uq_wallet_client_txn UNIQUE (wallet_id, client_txn_id);
