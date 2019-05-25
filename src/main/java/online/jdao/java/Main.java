package online.jdao.java;


import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args)throws IOException {
        try (Game g = new Game()){
            g.run();
        }
    }
}
