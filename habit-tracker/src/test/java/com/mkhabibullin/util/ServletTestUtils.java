package com.mkhabibullin.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServletTestUtils {
  
  public static class TestServletOutputStream extends ServletOutputStream {
    private final ByteArrayOutputStream outputStream;
    
    public TestServletOutputStream(ByteArrayOutputStream outputStream) {
      this.outputStream = outputStream;
    }
    
    @Override
    public boolean isReady() {
      return true;
    }
    
    @Override
    public void setWriteListener(WriteListener writeListener) {
    }
    
    @Override
    public void write(int b) throws IOException {
      outputStream.write(b);
    }
  }
  
  public static ServletOutputStream createServletOutputStream(ByteArrayOutputStream outputStream) {
    return new TestServletOutputStream(outputStream);
  }
}