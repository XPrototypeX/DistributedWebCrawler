package serializer;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

public class UrlSerializer implements StreamSerializer<Url> {

	public final static int StreamID = 1234;

	@Override
	public void destroy() {
		// DO nothing
	}

	@Override
	public int getTypeId() {

		return StreamID;
	}

	@Override
	public Url read(ObjectDataInput stream) throws IOException {

		String link = stream.readUTF();

		return new Url(link);
	}

	@Override
	public void write(ObjectDataOutput out, Url link) throws IOException {
		
		out.writeUTF(link.getUrl());

	}

}
