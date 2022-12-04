package hep.crest.server.controllers;

import java.io.InputStream;

public interface LobStreamerProvider {

    InputStream getInputStream();
}
