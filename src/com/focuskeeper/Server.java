package com.focuskeeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.ClientHandler;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Server extends NanoHTTPD {
	static final int PORT = 8000;
	
	public Server() {
		super(PORT);
		this.setAsyncRunner(new SingleThreadRunner());
	}
	
	public static String getAddr() {
		return "http://localhost:" + PORT;
	}
	
	public void run() throws IOException {
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}
	
	public Response serveFile(IHTTPSession session) {
		String filePath = session.getUri().replace("/static", "");
		filePath = filePath.replace("..", "");
		filePath = filePath.replace("//", "");
		filePath = "static" + filePath;

		String mimeType = getMimeTypeForFile(filePath);
		InputStream stream;
		try {
			stream = new FileInputStream(filePath);
		} catch(Exception e) {
			return newFixedLengthResponse(Status.NOT_FOUND, "text/html", "Not found");
		}
		long fileSize = new File(filePath).length();

		return newFixedLengthResponse(Status.OK, mimeType, stream, fileSize);
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		String uri = session.getUri();
		if(uri.startsWith("/static")) {
			return serveFile(session);
		}
		
		return newFixedLengthResponse(Status.NOT_FOUND, "text/html", "Not found");
	}
}

class SingleThreadRunner implements NanoHTTPD.AsyncRunner {
	@Override
	public void closeAll() {
	}

	@Override
	public void closed(ClientHandler clientHandler) {
	}

	@Override
	public void exec(ClientHandler clientHandler) {
		clientHandler.run();
	}
}