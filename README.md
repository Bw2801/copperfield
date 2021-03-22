# copperfield

A flexible, lightweight library to do ORMs using annotations (and magic).

![just copperfield things](./.gitlab/assets/copperfield.png)

[[_TOC_]]

## Setup

Import the `copperfield-core` module at the very least.

```xml
<dependency>
  <groupId>dev.benedikt</groupId>
  <artifactId>copperfield-core</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Bson

If you want to convert data from and to [bson documents](#bson-conversion), import the `copperfield-bson` module as well.

```xml
<dependency>
  <groupId>dev.benedikt</groupId>
  <artifactId>copperfield-bson</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Proto

If you want to convert data from and to [proto messages](#proto-conversion), import the `copperfield-proto` module as well.

```xml
<dependency>
  <groupId>dev.benedikt</groupId>
  <artifactId>copperfield-proto</artifactId>
  <version>2.0.0</version>
</dependency>
```

## Usage

### Value Conversion

At it's core, copperfield is a library to convert values of one datatype to values of another datatype defined in a so-called `Converter` and vice
versa.

```java
Converter<UUID, String> converter = new UuidToStringConverter();
converter.toTheirs(/* ... */); // Converts UUIDs to Strings
converter.toOurs(/* ... */); // Converts Strings to UUIDs
```

However, `Converters` require a bunch of parameters in order to be able to convert the values correctly in all circumstances. Because they are quite
delicate, they should not be set manually. This is where the `CopperfieldAgent` comes in. The agent provides a bunch of accessibility methods to
convert values.

```java
CopperfieldAgent agent = new CopperfieldAgent();
String uuidAsString = (String) agent.toTheirs(UUID.randomUUID());
UUID stringAsUuid = agent.toOurs(uuidAsString, UUID.class); 
```

The agent internally selects the converter matching the type, or the classes supertype, of the given value. The following converters are available by
default. All converters support conversion in both directions by default, however there are some exceptions.

| Our Type | Their Type | Converter | Comment |
| -------- | ---------- | --------- | ------- |
| `UUID` | `String` | `UuidToStringConverter` | Converts uuids to strings using `toString()`. |
| `Enum` | `String` | `EnumToStringConverter` | Converts enums to their corresponding names. |
| `OffsetDateTime` | `String` | `OffsetDateTimeToStringConverter` | Converts datetime instances to ISO 8601 strings. |
| `Number` | `Number` | `NumberConverter` | Makes sure that the number type does not get lost when converting `toOurs()`. |
| `Iterable` | `Iterable` | `IterableConverter` | Converts all values using the matching converters defined for the `CopperfieldAgent`. This will only work when converting `toTheirs` by default. See [POJO Conversion](#pojo-conversion). |
| `Map` | `Map` | `MapConverter` | Converts all keys and values using the matching converters defined for the `CopperfieldAgent`. This will only work when converting `toTheirs` by default. See [POJO Conversion](#pojo-conversion). |

### Converter Registries

`Registries` contain the required mappings and instantiated converters available to the agent. Every instantiated agent uses mappings defined in the
[BaseRegistry](copperfield-core/src/main/java/dev/volix/rewinside/odyssey/common/copperfield/registry/BaseRegistry.kt) by default.

You can pass additional `Registries` to the agent at construction time. This will [combine the registries](#combine-registries) and discard the
original instances afterwards.

```java
Registry registry1 = new CustomRegistry();
Registry registry2 = new AnotherCustomRegistry();
// ...
CopperfieldAgent agent = new CopperfieldAgent(registry, anotherCustomRegistry);
```

#### Add Converters

You may want to add additional converters. This can be achieved through calling the `with()` method on the registry.

```java
Registry registry = new BaseRegistry();

registry.with(UUID.class, UuidToUpperCaseStringConverter.class); // The registry will create a new instance on demand.
// or
registry.with(UUID.class, UuidToUpperCaseStringConverter()); // Injects a specific converter instance.
```

When working with the agent and you want to add converters dynamically, you can retrieve the agent's registry and call `with()` on that too.

```java
CopperfieldAgent agent = // ...
agent.getRegistry().with(UUID.class, UuidToUpperCaseStringConverter.class);
```

**Note**: If another converter already exists for the given type (e.g. `UUID`), the previous one will be overridden.

#### Remove Converters

To remove converters, you can use the `without()` method.

```java
Registry registry = // ...
registry.without(UUID.class); // Removes the converter currently assigned to UUIDs.
```

#### Combine Registries

For convenience, multiple `Registries` can be merged into a single one. This can be achieved using the corresponding `with()` method.

```java
Registry baseRegistry = new BaseRegistry();
Registry protoRegistry = new ProtoRegistry();
baseRegistry.with(protoRegistry);
```

In this example, all converter mappings and instances defined in the `protoRegistry` will be _copied_ to the `baseRegistry`. This will override
existing mappings in the `baseRegistry` if the same types are defined in both registries.

Accordingly, there is a `without()` method as well.

```java
CopperfieldAgent agent = new CopperfieldAgent(new ProtoRegistry());
agent.getRegistry().without(new BaseRegistry());
```

In this example, all converter mappings and instances defined in the `BaseRegistry` will be removed from the agent's registry if both types and
mapped converter classes are exactly the same.

#### Additional Converters

These converters exist but are not registered by default.

| Our Type | Their Type | Converter | Comment |
| -------- | ---------- | --------- | ------- |
| `Object` | `Object` | `NoOperationConverter` | Can be used to explicitly mark a type to not be converted. |

### Conversion Context

In the examples above, only one `Converter` could be defined for every given input type (e.g. every `UUID` will be converted to a `String`). However,
there might be instances where you do not want the UUIDs to be converted to String but rather to their binary representation. This is where `context`
comes into play.

For example:
```java
agent.toTheirs(uuid); // "Convert the UUID without context" (default converter)
agent.toTheirs(uuid, byte[].class); // "Convert the UUID in the context of a byte array."

agent.toOurs(string, UUID.class); // "Convert the value to a UUID without context" (default converter)
agent.toOurs(bytes, UUID.class, byte[].class); // "Convert the value to a UUID in the context of a byte array".
```

As you can see, the context basically defines "their" value type (i.e. the output type/format) or a matching supertype.

When [adding converters](#add-converters) to a `Registry`, you can optionally define the `context` for which the converters should be available.

```java
Registry registry = // ...
registry.with(UUID.class, UuidToByteArrayConverter.class, byte[].class);
```

The agent will use this converter for this context (in this case `byte[]`) or any of it's derived classes only. Otherwise, the agent will fall back
to any converter registered without any context.

[Removing converters](#remove-converters) while defining a context will remove this converter from the given context only. If there is no converter
registered specifically for this context, nothing will happen.

### POJO Conversion

Up until now we covered how to convert single values. Let's say you want to convert a POJO to an external data format (e.g. Bson Documents). You
_could_ write a custom `Converter` which handles conversion in both directions and register it in the `Registry`. But then you might want to convert
the same POJO to another data format as well. You would have to write new converters for every single data format, for every POJO you might want to
convert to and from.

This is where `CopperConvertables` come into play. This functionality allows you to define all class members you want to convert to/from once while
using the same definition for multiple different target formats.

Let's start with an example:

```java
public class TimedPartyEvent {

    private OffsetDateTime at;

    private PartyEventType type;
    
    private boolean someInternalFlag;

}
```

In this example we want the `TimedPartyEvent` to be converted into arbitrary target formats (`contexts`). The first step would be to implement the
`CopperConvertable` interface and annotate all fields we want to be included with the `@CopperField` annotation.

```java
public class TimedPartyEvent implements CopperConvertable {

    @CopperField
    private OffsetDateTime at;

    @CopperField
    private PartyEventType type;
    
    private boolean someInternalFlag;

}
```

Done, that's basically all there is to the basic POJO definition.

#### The @CopperField Annotation

The `@CopperField` annotation provides some properties to override or extend the functionality for each field.

| Name | Type | Description |
| ---- | ---- | ----------- |
| `name` | `String` | The name to use for the external formats. Defaults to the `snake_cased` field name. |
| `converter` | `Converter.class` | The converter to use for this field specifically. Defaults to the closest relative converter defined in the agent's registry for the context used. |
| `typeMapper` | `CopperTypeMapper.class` | The type mapper to use. Defaults to none. See [Advanced Usage / Type Mappers](#type-mappers). |

#### The @CopperFields Annotation

Instead of annotating all class members with `@CopperField`, you can annotate the class itself (or any of it's supertypes or implemented interfaces).
Individual fields can additionally be annotated with `@CopperField`, if functionality needs to be altered for those specifically.

#### The @CopperIgnore Annotation

To exclude a field from `@CopperFields` you can use the `@CopperIgnore` annotation without arguments. If you want to exclude this field for one or
more specific contexts, you can provide a list of matching context classes.

#### Iterables / Maps

Because generic types are only hints for the compiler, they are not available at runtime. This will cause values of `Iterables` to not be converted
back to their original types when converting `toOurs`. To fix this behavior, you may annotate the iterable field with `@CopperValueType`, defining
the same type defined in the generic parameter.

```java
@CopperField
@CopperValueType(UUID.class)
private final List<UUID> uuids = new ArrayList<>();
```

The `@CopperKeyType` annotation provides the same functionality for keys of `Map` fields.

### Bson Conversion

The `copperfield-bson` module implements provides the `BsonRegistry` containing these default converters for the `Document` context.

| Our Type | Their Type | Converter | Comment |
| -------- | ---------- | --------- | ------- |
| `CopperConvertable` | `Document` | `CopperToBsonConverter` | Converts classes implementing `CopperConvertable` to bson `Documents`. |
| `byte[]` | `Binary` | `ByteArrayToBsonBinaryConverter` | Converts byte arrays to the bson `Binary` format. |
| `ObjectId` | `ObjectId` | `NoOperationConverter` | Does not convert `ObjectIds` at all in the `Document` context. |

As well as this default converter when not converting in the `Document` context.

| Our Type | Their Type | Converter | Comment |
| -------- | ---------- | --------- | ------- |
| `ObjectId` | `String` | `BsonObjectIdToStringConverter` | Converts `ObjectIds` to the hex string representation. |

If you want to override the converter used for a specific field for the `Document` context only, you can annotate the `@CopperBsonField` annotation
in addition to `@CopperFields` or `@CopperField`. The annotation shares the signature with [the @CopperField annotation](#the-copperfield-annotation)
and will fall back to the values defined in the `@CopperField` annotation.

### Proto Conversion

The `copperfield-proto` module implements provides the `ProtoRegistry` containing these default converters for the `MessageLiteOrBuilder` context.

| Our Type | Their Type | Converter | Comment |
| -------- | ---------- | --------- | ------- |
| `CopperConvertable` | `MessageLiteOrBuilder` | `CopperToProtoConverter` | Converts classes implementing `CopperConvertable` to `MessageLiteOrBuilders`. |
| `byte[]` | `ByteString` | `ByteArrayToProtoByteStringConverter` | Converts byte arrays to the proto `ByteString` format. |
| `Map` | `Struct` | `MapToProtoStructConverter` | Converts `Maps` to proto `Structs`. |

This converter exists but is not registered by default.

| Our Type | Their Type | Converter | Comment |
| -------- | ---------- | --------- | ------- |
| `OffsetDateTime` | `Timestamp` | `OffsetDateTimeToProtoTimestampConverter` | Converts `OffsetDateTimes` to proto `Timestamps` using the given timezone. |

If you want to override the converter used for a specific field for the `MessageLiteOrBuilder` context only, you can annotate the
`@CopperProtoField` annotation in addition to `@CopperFields` or `@CopperField`. The annotation shares the signature with
[the @CopperField annotation](#the-copperfield-annotation) and will fall back to the values defined in the `@CopperField` annotation.

**Note**: To convert `CopperConvertables` to and from proto messages, you _have to_ annotate the class with `@CopperProtoClass`. The given type
should be a type diverging from `MessageLiteOrBuilder` which this POJO represents.

```java
@CopperFields
@CopperProtoField(type = PartyProtos.PartyEvent)
public class TimedPartyEvent implements CopperConvertable {

    private OffsetDateTime at;

    private PartyEventType type;

}
```

## Advanced Usage

### Type Mappers

The explanation will be based on the following example.

```java
@CopperFields
@CopperProtoField(type = PartyProtos.PartyEvent)
public class TimedPartyEvent implements CopperConvertable {

    private OffsetDateTime at;

    private PartyEventType type;

    private PartyEvent event;

}
```

Let's assume `PartyEvent` is an interface. Converting the event `toTheirs` will work no problem because the agent will use the value's concrete
type. However, converting it back `toOurs` will lose the concrete `PartyEvent` type resulting in an exception, because the `PartyEvent` interface
can not be instantiated.

To map the interface to a concrete class, copperfield provides `CopperTypeMappers`.

```java
public class PartyEventTypeMapper extends CopperTypeMapper<TimedPartyEvent, PartyEvent> {

    public PartyEventCopperTypeMapper() {
        super("type");
    }

    @NotNull @Override
    public Class<? extends PartyEvent> mapType(final TimedPartyEvent instance, @NotNull final Class<?> valueType) {
        return instance.type.type;
    }

}
```

The type mapper declares, which fields are essential to decide on the concrete class. Copperfield makes sure that the required fields will be
populated on the `instance` if their corresponding values are not null and as long as there are no cylcic field requirements.
