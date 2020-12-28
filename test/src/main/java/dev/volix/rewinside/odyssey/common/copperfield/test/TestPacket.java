package dev.volix.rewinside.odyssey.common.copperfield.test;

import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperField;
import dev.volix.rewinside.odyssey.common.copperfield.bson.BsonConvertible;
import dev.volix.rewinside.odyssey.common.copperfield.bson.BsonRegistry;
import dev.volix.rewinside.odyssey.common.copperfield.protobuf.ProtoConvertible;
import dev.volix.rewinside.odyssey.common.copperfield.protobuf.ProtoRegistry;
import dev.volix.rewinside.odyssey.hagrid.protocol.Packet;
import dev.volix.rewinside.odyssey.hagrid.protocol.StatusCode;
import java.util.UUID;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * @author Benedikt Wüller
 */
public class TestPacket implements BsonConvertible, ProtoConvertible<Packet> {

    @CopperField(name = "id")
    private UUID id;

    @CopperField(name = "request_id")
    private UUID requestId;

    @CopperField(name = "status")
    private Status status;

    @CopperField(name = "payload")
    private Payload payload;

    @Override @NotNull
    public Class<Packet> getProtoClass() {
        return Packet.class;
    }

    public static class Status implements BsonConvertible, ProtoConvertible<dev.volix.rewinside.odyssey.hagrid.protocol.Status> {

        @CopperField(name = "code")
        private StatusCode code;

        @CopperField(name = "message")
        private String message;

        @Override @NotNull
        public Class<dev.volix.rewinside.odyssey.hagrid.protocol.Status> getProtoClass() {
            return dev.volix.rewinside.odyssey.hagrid.protocol.Status.class;
        }

    }

    public static class Payload implements BsonConvertible, ProtoConvertible<Packet.Payload> {

        @CopperField(name = "type_url")
        private String typeUrl;

        @CopperField(name = "value")
        private byte[] value;

        @Override @NotNull
        public Class<Packet.Payload> getProtoClass() {
            return Packet.Payload.class;
        }

    }

    public static void main(String[] args) {
        final ProtoRegistry protoRegistry = new ProtoRegistry();
        final BsonRegistry bsonRegistry = new BsonRegistry();

        final TestPacket testPacket = new TestPacket();
        testPacket.id = UUID.randomUUID();
        testPacket.requestId = UUID.randomUUID();

        testPacket.status = new Status();
        testPacket.status.code = StatusCode.OK;
        testPacket.status.message = "Success";

        testPacket.payload = new Payload();
        testPacket.payload.typeUrl = "https://www.google.com";
        testPacket.payload.value = new byte[] { 4, 2, 0, 6, 9 };

        final Packet packet = testPacket.toProtoMessage(protoRegistry);
        System.out.println(packet);

        final Document document = testPacket.toBsonDocument(bsonRegistry);
        System.out.println(document);

        final TestPacket protoResult = new TestPacket();
        protoResult.fromProtoMessage(packet, protoRegistry);

        final TestPacket bsonResult = new TestPacket();
        bsonResult.fromBsonDocument(document, bsonRegistry);

        final String foo = "bar";
    }

}
