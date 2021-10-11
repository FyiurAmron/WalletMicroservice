package money.wallet.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( value = HttpStatus.BAD_REQUEST, reason = "illegal amount" )
public class IllegalOperationAmountException extends IllegalArgumentException {
    public IllegalOperationAmountException( String msg ) {
        super( msg );
    }
}
