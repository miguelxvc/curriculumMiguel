@SuppressWarnings("unchecked")
Number insertPerson(Person person) {
    Map<String, Object> params = new HashMap<>();
    params.putAll(objectMapper.convertValue(person, Map.class));

    DatabaseRequestDTO request = DatabaseRequestDTO.builder()
        .catalogName(BacConstant.CATALOG_NAME)
        .schemaName(BacConstant.SCHEMA_NAME_DBO)
        .sql("spAddChangingClientAndData")
        .params(params)
        .build();

    var responseQuery = piafDatabaseConnection.getProcedureGeneralPiaf(request);

    // Procesar la respuesta y extraer el número directamente
    Number clientSequence = Optional.ofNullable(responseQuery.getObject())
        .filter(obj -> obj instanceof Map<?, ?>)
        .map(obj -> (Map<?, ?>) obj)
        .map(responseMap -> (List<Map<String, Object>>) responseMap.get(BacConstant.RESULT_SET))
        .filter(resultSet -> resultSet != null && !resultSet.isEmpty())
        .map(resultSet -> resultSet.get(0))
        .map(firstRecord -> {
            // Opción 1: Si el número está en un campo específico (ej: "id", "sequence", "clientId")
            Object sequenceValue = firstRecord.get("clientSequence"); // Ajusta el nombre del campo según tu BD
            if (sequenceValue instanceof Number) {
                return (Number) sequenceValue;
            }
            // Opción 2: Si necesitas convertir desde String
            if (sequenceValue instanceof String) {
                try {
                    return Long.parseLong((String) sequenceValue);
                } catch (NumberFormatException e) {
                    PiafLogger.error("Error parsing sequence value: " + sequenceValue, e);
                    return null;
                }
            }
            return null;
        })
        .orElse(null);

    PiafLogger.debug("clientSequence: " + clientSequence);

    return clientSequence;
}

// Alternativa más simple si sabes el nombre exacto del campo:
@SuppressWarnings("unchecked")
Number insertPersonAlternative(Person person) {
    Map<String, Object> params = new HashMap<>();
    params.putAll(objectMapper.convertValue(person, Map.class));

    DatabaseRequestDTO request = DatabaseRequestDTO.builder()
        .catalogName(BacConstant.CATALOG_NAME)
        .schemaName(BacConstant.SCHEMA_NAME_DBO)
        .sql("spAddChangingClientAndData")
        .params(params)
        .build();

    var responseQuery = piafDatabaseConnection.getProcedureGeneralPiaf(request);

    // Versión más directa si conoces el campo específico
    Number clientSequence = Optional.ofNullable(responseQuery.getObject())
        .filter(obj -> obj instanceof Map<?, ?>)
        .map(obj -> (Map<?, ?>) obj)
        .map(responseMap -> (List<Map<String, Object>>) responseMap.get(BacConstant.RESULT_SET))
        .filter(resultSet -> resultSet != null && !resultSet.isEmpty())
        .map(resultSet -> (Number) resultSet.get(0).get("clientSequence")) // Cambia "clientSequence" por el nombre real del campo
        .orElse(null);

    PiafLogger.debug("clientSequence: " + clientSequence);

    return clientSequence;
}

// Si ClientSequences tiene un método getter para obtener el número:
@SuppressWarnings("unchecked")
Number insertPersonWithClientSequences(Person person) {
    Map<String, Object> params = new HashMap<>();
    params.putAll(objectMapper.convertValue(person, Map.class));

    DatabaseRequestDTO request = DatabaseRequestDTO.builder()
        .catalogName(BacConstant.CATALOG_NAME)
        .schemaName(BacConstant.SCHEMA_NAME_DBO)
        .sql("spAddChangingClientAndData")
        .params(params)
        .build();

    var responseQuery = piafDatabaseConnection.getProcedureGeneralPiaf(request);

    // Crear el objeto ClientSequences y luego extraer el número
    ClientSequences clientSequences = Optional.ofNullable(responseQuery.getObject())
        .filter(obj -> obj instanceof Map<?, ?>)
        .map(obj -> (Map<?, ?>) obj)
        .map(responseMap -> (List<Map<String, Object>>) responseMap.get(BacConstant.RESULT_SET))
        .filter(resultSet -> resultSet != null && !resultSet.isEmpty())
        .map(resultSet -> objectMapper.convertValue(resultSet.get(0), ClientSequences.class))
        .orElse(null);

    PiafLogger.debug("clientSequences: " + clientSequences);

    // Extraer el número del objeto ClientSequences
    return clientSequences != null ? clientSequences.getSequence() : null; // Ajusta el método getter según tu clase
}