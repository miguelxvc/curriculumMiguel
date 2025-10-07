package com.bac.piaf.catalogs.controller.request;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityRequestDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    void lombokBuilderAndGettersSetters_workCorrectly() {
        // Usar builder para crear instancia
        ActivityRequestDTO dto = ActivityRequestDTO.builder()
                .activityId(123)
                .activityName("Test Activity")
                .companyId(456)
                .build();

        // Verificar getters
        assertEquals(123, dto.getActivityId());
        assertEquals("Test Activity", dto.getActivityName());
        assertEquals(456, dto.getCompanyId());

        // Verificar setters
        dto.setActivityId(321);
        dto.setActivityName("Another");
        dto.setCompanyId(654);
        assertEquals(321, dto.getActivityId());
        assertEquals("Another", dto.getActivityName());
        assertEquals(654, dto.getCompanyId());

        // toString contiene los valores
        String repr = dto.toString();
        assertTrue(repr.contains("activityId=321"));
        assertTrue(repr.contains("activityName=Another"));
        assertTrue(repr.contains("companyId=654"));
    }

    @Test
    void jsonSerialization_includesOnlyNonNullFields() throws Exception {
        // Sólo activityName no nulo
        ActivityRequestDTO dto = ActivityRequestDTO.builder()
                .activityName("SoloName")
                .build();

        String json = objectMapper.writeValueAsString(dto);
        JsonNode root = objectMapper.readTree(json);

        // Debe contener activityName
        assertTrue(root.has("activityName"));
        assertEquals("SoloName", root.get("activityName").asText());

        // activityId y companyId deben omitirse (nulos)
        assertFalse(root.has("activityId"));
        assertFalse(root.has("companyId"));
    }

    @Test
    void jsonSerialization_withAllFields() throws Exception {
        ActivityRequestDTO dto = ActivityRequestDTO.builder()
                .activityId(1)
                .activityName("Name")
                .companyId(2)
                .build();

        String json = objectMapper.writeValueAsString(dto);
        JsonNode root = objectMapper.readTree(json);

        assertEquals(1, root.get("activityId").asInt());
        assertEquals("Name", root.get("activityName").asText());
        assertEquals(2, root.get("companyId").asInt());
    }

    @Test
    void jsonDeserialization_missingOptionalFields() throws Exception {
        // JSON sin activityId ni companyId
        String json = """
                {"activityName":"OnlyName"}
                """;

        ActivityRequestDTO dto = objectMapper.readValue(json, ActivityRequestDTO.class);
        assertNull(dto.getActivityId(), "activityId debe ser nulo");
        assertEquals("OnlyName", dto.getActivityName());
        assertNull(dto.getCompanyId(), "companyId debe ser nulo");
    }

    @Test
    void jsonDeserialization_withAllFields() throws Exception {
        String json = """
                {
                    "activityId": 99,
                    "activityName": "Full",
                    "companyId": 100
                }
                """;

        ActivityRequestDTO dto = objectMapper.readValue(json, ActivityRequestDTO.class);
        assertEquals(99, dto.getActivityId());
        assertEquals("Full", dto.getActivityName());
        assertEquals(100, dto.getCompanyId());
    }
}