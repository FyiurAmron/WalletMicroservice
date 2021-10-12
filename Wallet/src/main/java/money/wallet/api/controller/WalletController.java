package money.wallet.api.controller;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import money.wallet.api.service.WalletService;
import money.wallet.api.data.WalletOperation;
import money.wallet.api.exception.*;
import money.wallet.api.data.WalletStatement;

@RequiredArgsConstructor
@RestController
@RequestMapping( "v0/wallet" )
public class WalletController {
    final private WalletService walletService;

    // TODO document @ApiResponses etc.

    @PostMapping( "" )
    @ResponseStatus( code = HttpStatus.CREATED )
    public WalletOperation create(
            @RequestParam( value = "transactionId" ) long transactionId
    ) throws TransactionIdAlreadyExistsException {
        return walletService.createWallet( transactionId );
    }

    /*
    @DeleteMapping( "/" )
    public WalletResponse remove() {
        return walletService.removeWallet();
    }
    */

    @GetMapping( "/{walletId}" )
    public WalletOperation balance(
            @PathVariable( value = "walletId" ) long walletId
    ) throws WalletIdNotFoundException {
        return walletService.getBalance( walletId );
    }

    @GetMapping( "/{walletId}/statement" )
    public WalletStatement statement(
            @PathVariable( value = "walletId" ) long walletId
    ) throws WalletIdNotFoundException {
        // TODO (if needed) paging/sorting via query string etc. - already provided by the service
        return walletService.getStatement( walletId );
    }

    @PostMapping( "/{walletId}/deposit" )
    public WalletOperation deposit(
            @PathVariable( value = "walletId" ) long walletId,
            @RequestParam( value = "amount" ) long amount,
            @RequestParam( value = "transactionId" ) long transactionId
    ) throws WalletIdNotFoundException,
             IllegalOperationAmountException,
             TransactionIdAlreadyExistsException {
        return walletService.makeDeposit( walletId, amount, transactionId );
    }

    @PostMapping( "/{walletId}/withdrawal" )
    public WalletOperation withdrawal(
            @PathVariable( value = "walletId" ) long walletId,
            @RequestParam( value = "amount" ) long amount,
            @RequestParam( value = "transactionId" ) long transactionId
    ) throws WalletIdNotFoundException,
             IllegalOperationAmountException,
             TransactionIdAlreadyExistsException {
        return walletService.makeWithdrawal( walletId, amount, transactionId );
    }
}
