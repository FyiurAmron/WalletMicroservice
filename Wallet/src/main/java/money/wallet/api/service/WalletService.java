package money.wallet.api.service;

import money.wallet.api.data.WalletOperation;
import money.wallet.api.data.WalletStatement;
import money.wallet.api.exception.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface WalletService {
    WalletOperation createWallet( long transactionId )
            throws TransactionIdAlreadyExistsException;

    // WalletOperation removeWallet();

    WalletOperation getBalance( long walletId )
            throws WalletIdNotFoundException;

    WalletStatement getStatement( long walletId )
            throws WalletIdNotFoundException;

    WalletStatement getStatement( long walletId, Sort sort )
            throws WalletIdNotFoundException;

    WalletStatement getStatement( long walletId, Pageable pageable )
            throws WalletIdNotFoundException;

    WalletOperation makeDeposit( long walletId, long amount, long transactionId )
            throws WalletIdNotFoundException,
                   IllegalOperationAmountException,
                   TransactionIdAlreadyExistsException;

    WalletOperation makeWithdrawal( long walletId, long amount, long transactionId )
            throws WalletIdNotFoundException,
                   IllegalOperationAmountException,
                   TransactionIdAlreadyExistsException;
}
