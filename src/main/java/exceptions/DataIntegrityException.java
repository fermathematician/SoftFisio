// Local: src/db/exceptions/DataIntegrityException.java

package db.exceptions;

/**
 * Exceção personalizada para ser lançada quando uma operação de banco de dados
 * viola uma regra de integridade de dados (como deletar um pai que tem filhos).
 */
public class DataIntegrityException extends RuntimeException {

    public DataIntegrityException(String msg) {
        super(msg);
    }
}