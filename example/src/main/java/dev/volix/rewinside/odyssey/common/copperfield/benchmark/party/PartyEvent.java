package dev.volix.rewinside.odyssey.common.copperfield.benchmark.party;

import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperField;
import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperMapTypes;
import dev.volix.rewinside.odyssey.common.copperfield.bson.BsonConvertable;
import dev.volix.rewinside.odyssey.common.copperfield.converter.ZonedDateTimeToStringConverter;
import dev.volix.rewinside.odyssey.common.copperfield.protobuf.ProtoConvertable;
import dev.volix.rewinside.odyssey.common.copperfield.protobuf.annotation.CopperProtoField;
import dev.volix.rewinside.odyssey.common.copperfield.protobuf.annotation.CopperProtoType;
import dev.volix.rewinside.odyssey.hagrid.protocol.party.PartyProtos;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Benedikt Wüller
 */
@CopperProtoType(type = PartyProtos.PartyEvent.class)
public class PartyEvent implements BsonConvertable, ProtoConvertable<PartyProtos.PartyEvent> {

    @CopperField(name = "at")
    @CopperProtoField(converter = ZonedDateTimeToStringConverter.class)
    public ZonedDateTime at;

    @CopperField(name = "type")
    public String type;

    @CopperField(name = "details")
    @CopperMapTypes(keyType = String.class, valueType = Object.class)
    public Map<String, Object> details = new HashMap<>();

}