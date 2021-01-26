package dev.volix.rewinside.odyssey.common.copperfield.example;

import dev.volix.rewinside.odyssey.common.copperfield.CopperConvertable;
import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperCollectionType;
import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperFields;
import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperIgnore;
import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperValueType;
import dev.volix.rewinside.odyssey.common.copperfield.proto.annotation.CopperProtoClass;
import dev.volix.rewinside.odyssey.hagrid.protocol.party.PartyProtos;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * @author Benedikt Wüller
 */
@CopperFields
@CopperProtoClass(type = PartyProtos.Party.class)
public class Party implements CopperConvertable {

    public UUID id;

    public OffsetDateTime createdAt;

    public OffsetDateTime disbandedAt;

    public UUID leaderUuid;

    @CopperValueType(type = PartyMember.class)
    public List<PartyMember> members = new ArrayList<>();

    public PartySettings settings = new PartySettings();

    @CopperValueType(type = UUID.class)
    @CopperCollectionType(type = TreeSet.class)
    public Set<UUID> bannedUuids = new HashSet<>();

    @CopperValueType(type = TimedPartyEvent.class)
    public List<TimedPartyEvent> events = new ArrayList<>();

    @CopperIgnore
    public boolean deserialized = false;

    @Override
    public void onAfterTheirsToOurs() {
        this.deserialized = true;
    }

}
