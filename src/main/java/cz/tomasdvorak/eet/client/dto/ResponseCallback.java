package cz.tomasdvorak.eet.client.dto;

import cz.tomasdvorak.eet.client.exceptions.CommunicationException;
import cz.tomasdvorak.eet.client.exceptions.CommunicationTimeoutException;

/**
 * Callback for asynchronous calls to the EET webservice
 */
public interface ResponseCallback {
    /**
     * Call succeeded without any exception
     * @param result request and result data
     */
    void onComplete(final SubmitResult result);

    /**
     * Call failed during execution
     * @param cause contains original exception and all the request data like PKP for offline mode functionality
     */
    void onError(final CommunicationException cause);

    /**
     * Call timeout during communication with the webservice.
     * @param cause contains original exception and all the request data like PKP for offline mode functionality
     */
    void onTimeout(final CommunicationTimeoutException cause);

}
