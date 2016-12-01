package cz.tomasdvorak.eet.client.security;

import cz.etrzby.xml.TrzbaDataType;
import cz.tomasdvorak.eet.client.exceptions.DataSigningException;
import cz.tomasdvorak.eet.client.utils.DateUtils;
import cz.tomasdvorak.eet.client.utils.NumberUtils;
import cz.tomasdvorak.eet.client.utils.StringJoiner;
import cz.tomasdvorak.eet.client.utils.StringUtils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for the BKP and PKP signing and hashing
 * PKP - Podpisový kód poplatníka/Taxpayer's Signature Code
 * BKP - Bezpečnostní kód poplatníka/Taxpayer's Security Code
 */
public class SecurityCodesGenerator {

    private final ClientKey clientKey;

    public SecurityCodesGenerator(final ClientKey clientKey) {
        this.clientKey = clientKey;
    }

     /**
     * BKP - hash of the PKP signature.
     * @param pkp earlier computed PKP
     * @return a hash code in base16 notation, split by blocks of 8 chars, separated by a hyphen (for example 9356D566-A3E48838-FB403790-D201244E-95DCBD92).
     * @throws DataSigningException when there is a problem with the private key
     */    
    public String getBKP(final byte[] pkp) throws DataSigningException {
            final byte[] bytes = sha1(pkp);
            final String base16 = StringUtils.leftPad(toBase16(bytes), 40, '0');
            final String[] blocks = StringUtils.splitBlocks(base16, 8);
            final StringJoiner stringJoiner = new StringJoiner("-");
            for(final String block : blocks) {
                stringJoiner.add(block);
            }
            return stringJoiner.toString().toUpperCase();
    }

    private String toBase16(final byte[] bytes) {
        return new BigInteger(1, bytes).toString(16);
    }

    /**
     * Compute the signature of the receipt data
     */
    public byte[] getPKP(final TrzbaDataType data) throws DataSigningException {
        return this.clientKey.sign(serializeData(data));
    }

    private static String serializeData(final TrzbaDataType data) {
        final StringJoiner joiner = new StringJoiner("|");
        joiner.add(data.getDicPopl());
        joiner.add("" + data.getIdProvoz());
        joiner.add(data.getIdPokl());
        joiner.add(data.getPoradCis());
        joiner.add(DateUtils.format(data.getDatTrzby()));
        joiner.add(NumberUtils.format(data.getCelkTrzba()));
        return joiner.toString();
    }

    private static byte[] sha1(final byte[] text) throws DataSigningException {
        try {
            final java.security.MessageDigest d = java.security.MessageDigest.getInstance("SHA-1");
            d.reset();
            d.update(text);
            return d.digest();
        } catch (final NoSuchAlgorithmException e) {
            throw new DataSigningException(e);
        }
    }
}
