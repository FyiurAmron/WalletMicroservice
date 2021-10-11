package money.wallet.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityExistsException;

@ResponseStatus( value = HttpStatus.CONFLICT, reason = "transaction ID already exists" )
public class TransactionIdAlreadyExistsException extends EntityExistsException {

    public TransactionIdAlreadyExistsException( long transactionId ) {
        super( "wallet transaction with ID '" + transactionId + "' already present in DB" );
    }
}
