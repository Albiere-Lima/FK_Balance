package br.ufpb.dcx.lima.albiere.fK_Balance.exeptions;

public class MissingEconomySettingsException extends RuntimeException {
    public MissingEconomySettingsException(String message) {
        super(message);
    }
}
