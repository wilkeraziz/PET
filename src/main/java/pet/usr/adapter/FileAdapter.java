/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.usr.adapter;

import java.io.File;

/**
 * A FielAdapter simply encapsulates a File and implements the Comparable interface.
 * It's mostly used in the code of the main page to list context files.
 * 
 * @author waziz
 */
public class FileAdapter implements Comparable<FileAdapter> {
    
    private final File file;

    public FileAdapter(final File file){
        this.file = file;
    }

    @Override
    public String toString(){
        return file.getName();
    }
    

    public File getFile(){
        return file;
    }

    @Override
    public int compareTo(final FileAdapter other){
        return toString().compareTo(other.toString());
    }

}
