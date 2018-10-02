package Domainlogic;

import DomainObjects.BootstrapInformation;
import Service.DataSaver;
import Service.Exceptions.DataSaveException;

import java.io.FileNotFoundException;

public class BootstrapManager {

    private final String BOOTSTRAP_INFO_FILE = "BootstrapInformation.ser";

    private BootstrapInformation bootstrapInfo;

    public BootstrapManager() throws DataSaveException {
        loadBootstrapInformation();
    }

    public BootstrapInformation getBootstrapInfo() {
        return bootstrapInfo;
    }

    public void setBootstrapInfo(BootstrapInformation bootstrapInfo) throws DataSaveException {
        this.bootstrapInfo = bootstrapInfo;
        save();
    }

    public boolean isBootstrapInfoEmpty() {
        return bootstrapInfo == null;
    }

    public void save() throws DataSaveException {
        DataSaver<BootstrapInformation> saver = new DataSaver<>(BOOTSTRAP_INFO_FILE);
        saver.saveData(bootstrapInfo);
    }

    private void loadBootstrapInformation() throws DataSaveException {
        DataSaver<BootstrapInformation> saver = new DataSaver<>(BOOTSTRAP_INFO_FILE);
        try {
            bootstrapInfo = saver.loadData();
        } catch (FileNotFoundException e) {
            bootstrapInfo = null;
        }
    }
}
