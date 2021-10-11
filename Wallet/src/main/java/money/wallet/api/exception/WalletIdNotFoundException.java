package money.wallet.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ResponseStatus( value = HttpStatus.NOT_FOUND, reason = "wallet with given ID not found" )
public class WalletIdNotFoundException extends EntityNotFoundException {
    public WalletIdNotFoundException( long walletId ) {
        super( "wallet ID '" + walletId + "' not found" );
    }
}
