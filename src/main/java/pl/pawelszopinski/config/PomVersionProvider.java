package pl.pawelszopinski.config;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import picocli.CommandLine.IVersionProvider;
import pl.pawelszopinski.exception.ReadPomFileException;

import java.io.FileReader;
import java.io.IOException;

public class PomVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model;
        try {
            model = reader.read(new FileReader("pom.xml"));
        } catch (IOException | XmlPullParserException e) {
            throw new ReadPomFileException("Unable to retrieve application version.");
        }

        return new String[]{"Version: " + model.getVersion()};
    }
}
