package liqp.spi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import liqp.TemplateContext;
import liqp.filters.date.CustomDateFormatSupport;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;


public class Java8DateTypesSupport extends BasicTypesSupport {
    @Override
    public void configureTypes(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("liqp java 8 date type support");

        // todo: generic way for all java8 temporal types? or just list all
        registerType(module, ZonedDateTime.class, new TypeConvertor<ZonedDateTime>(){
            @Override
            public void serialize(JsonGenerator gen, ZonedDateTime val) throws IOException {
                gen.writeNumberField("val", val.toInstant().toEpochMilli());
                gen.writeStringField("zone", val.getZone().getId());
            }

            @Override
            public ZonedDateTime deserialize(TemplateContext context, Map node) {
                long val = (Long) node.get("val");
                Instant inst = Instant.ofEpochMilli(val);
                ZoneId zone = ZoneId.of((String) node.get("zone"));
                return ZonedDateTime.ofInstant(inst, zone);
            }

        });
        mapper.registerModule(module);
    }
}