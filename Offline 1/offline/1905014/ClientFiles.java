import util.NetworkUtil;

import java.io.*;

public class ClientFiles {
    private NetworkUtil networkUtil;

    ClientFiles(NetworkUtil networkUtil){
        this.networkUtil=networkUtil;
    }

    void fileUpload(Upload upload) throws IOException {
        int chunk=upload.getChunk();
        String filepath=upload.getFilepath();
        int fileID=upload.getFileID();
        File file = new File(filepath);
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try {
            int totalBytesRead = 0;

            while ( totalBytesRead < file.length() )
            {
                int bytesRemaining = (int) (file.length()-totalBytesRead);

                if ( bytesRemaining < chunk )
                {
                    chunk = bytesRemaining;
                    upload.setComplete(true);
                }
                byte[] buffer = new byte[chunk];
                int bytesRead = bufferedInputStream.read(buffer, 0, chunk);

                if ( bytesRead > 0) // If bytes read is not empty
                {
                    totalBytesRead += bytesRead;
                }
                upload.setBuffer(buffer);
                networkUtil.write(upload);

                boolean received=false;

                try{
                    Object o=networkUtil.readTimed();
                    if (o instanceof Ack) {
                        received = true;
                    }

                }catch (Exception e){
                    System.out.println(e);
                }

                if(!received){
                    upload.setFail(true);
                    System.out.println("Upload failed");
                    networkUtil.write(upload);
                    break;
                }
            }
        }catch (IOException e){
            System.out.println("File upload error");
        }
        System.out.println("Uploaded to server");
        bufferedInputStream.close();
    }

    void fileDownload(Download download) throws IOException {
        String name=download.getName();
        File file=new File(download.getSavePath(), download.getName());
        file.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        try {
            bufferedOutputStream.write(download.getBuffer(), 0, download.getBuffer().length);
            while (!download.isComplete()) {
                Object o = networkUtil.read();
                if(o instanceof Download){
                    download=(Download) o;
                    bufferedOutputStream.write(download.getBuffer(), 0, download.getBuffer().length);
                }
            }
            System.out.println("Download is completed");
        }catch (Exception e){
            System.out.println(e);
        }
        bufferedOutputStream.close();
    }
}
