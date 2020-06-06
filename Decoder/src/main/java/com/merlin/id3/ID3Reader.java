package com.merlin.id3;

import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.filename.FilenameTag;
import org.farng.mp3.filename.FilenameTagBuilder;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v1_1;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_3;
import org.farng.mp3.id3.ID3v2_4;
import org.farng.mp3.lyrics3.AbstractLyrics3;
import org.farng.mp3.lyrics3.Lyrics3v1;
import org.farng.mp3.lyrics3.Lyrics3v2;

import java.io.File;
import java.io.RandomAccessFile;

public class ID3Reader {

    public void dd(RandomAccessFile random){
         AbstractID3v2 id3v2tag=null;
         AbstractLyrics3 lyrics3tag=null;
         File mp3file;
         FilenameTag filenameTag;
         ID3v1 id3v1tag=null;
         try {
            id3v1tag = new ID3v1_1(random);
            } catch (Exception var12) {
        }
        try {
            if (id3v1tag == null) {
                id3v1tag = new ID3v1(random);
            }
        } catch (Exception var11) {
        }
        try {
            id3v2tag = new ID3v2_4(random);
        } catch (Exception var10) {
        }
        try {
            if (id3v2tag == null) {
                id3v2tag = new ID3v2_3(random);
            }
        } catch (Exception var9) {
        }

        try {
            if (id3v2tag == null) {
                id3v2tag = new ID3v2_2(random);
            }
        } catch (Exception var8) {
        }

        try {
            lyrics3tag = new Lyrics3v2(random);
        } catch (Exception var7) {
        }

        try {
            if (lyrics3tag == null) {
                lyrics3tag = new Lyrics3v1(random);
            }
        } catch (Exception var6) {
        }
    }
}
