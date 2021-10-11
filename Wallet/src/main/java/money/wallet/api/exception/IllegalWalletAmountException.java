package money.wallet.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( value = HttpStatus.BAD_REQUEST, reason = "illegal amount" )
public class IllegalWalletAmountException extends IllegalArgumentException {
    public IllegalWalletAmountException( String msg ) {
        super( msg );
    }
}
