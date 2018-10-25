package Service;

import Service.Exceptions.DataSaveException;

import java.io.*;

public class DataSaver<T extends Serializable> {

    private final String filePath;

    public DataSaver(String fileName) throws DataSaveException {
        final String DATA_FOLDER = System.getenv("APPDATA");
        final String APPLICATION_FOLDER_NAME = "ChatSki";
        File appDirectory = new File(DATA_FOLDER + "\\" + APPLICATION_FOLDER_NAME);

        if (!appDirectory.exists()) {
            boolean success = appDirectory.mkdir();
            if (!success) {
                throw new DataSaveException("Could not create directory");
            }
        }

        filePath = DATA_FOLDER + "\\" + APPLICATION_FOLDER_NAME + "\\" + fileName;
    }

    public void saveData(T data) throws DataSaveException {
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    throw new DataSaveException("Could not create file");
                }
            } catch (IOException e) {
                throw new DataSaveException("Could not create file");
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(data);
            } catch (IOException e) {
                throw new DataSaveException("Could not write to file");
            }
        } catch (IOException e) {
            throw new DataSaveException("File not found");
        }
    }

    @SuppressWarnings("unchecked")
    public T loadData() throws FileNotFoundException, DataSaveException {
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream((inputStream))) {
                return (T)objectInputStream.readObject();
            } catch (IOException|ClassNotFoundException e) {
                throw new DataSaveException("Could not read from file");
            }
        } catch (IOException e) {
            throw new FileNotFoundException("File not found");
        }
    }
}
