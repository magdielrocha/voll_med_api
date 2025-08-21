package med.voll.api.domain;

public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem); //chamando a classe mãe com super, passando a mensagem
    }

}
