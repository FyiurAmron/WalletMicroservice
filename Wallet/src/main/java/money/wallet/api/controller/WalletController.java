package money.wallet.api.controller;

import lombok.*;
import money.wallet.api.data.WalletOperation;
import money.wallet.api.service.RepositoryWalletService;
import money.wallet.api.data.WalletStatement;
import org.springframework.web.bind.annotation.*;

// TODO return HTTP error codes as needed
@RequiredArgsConstructor
@RestController
@RequestMapping( "v0/wallet" )
public class WalletController {
    final private RepositoryWalletService walletService;

    @PostMapping( "/" )
    public WalletOperation create( @RequestParam( value = "transactionId" ) long transactionId ) {
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
    ) {
        return walletService.getBalance( walletId );
    }

    @GetMapping( "/{walletId}/statement" )
    public WalletStatement statement(
            @PathVariable( value = "walletId" ) long walletId
    ) {
        // TODO (if needed) paging/sorting via query string etc. - already provided by the service
        return walletService.getStatement( walletId );
    }

    @PostMapping( "/{walletId}/deposit" )
    public WalletOperation deposit(
            @PathVariable( value = "walletId" ) long walletId,
            @RequestParam( value = "amount" ) long amount,
            @RequestParam( value = "transactionId" ) long transactionId
    ) {
        return walletService.makeDeposit( walletId, amount, transactionId );
    }

    @PostMapping( "/{walletId}/withdrawal" )
    public WalletOperation withdrawal(
            @PathVariable( value = "walletId" ) long walletId,
            @RequestParam( value = "amount" ) long amount,
            @RequestParam( value = "transactionId" ) long transactionId
    ) {
        return walletService.makeWithdrawal( walletId, amount, transactionId );
    }
}
