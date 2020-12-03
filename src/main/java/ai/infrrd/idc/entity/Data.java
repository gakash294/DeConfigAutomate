package ai.infrrd.idc.entity;

public class Data {
    String id;
    String fieldName;
    String extractorFieldName;
    String extractorType;
    String extractionProcessType;
    String microserviceFieldConfig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getExtractorFieldName() {
        return extractorFieldName;
    }

    public void setExtractorFieldName(String extractorFieldName) {
        this.extractorFieldName = extractorFieldName;
    }

    public String getExtractorType() {
        return extractorType;
    }

    public void setExtractorType(String extractorType) {
        this.extractorType = extractorType;
    }

    public String getExtractionProcessType() {
        return extractionProcessType;
    }

    public void setExtractionProcessType(String extractionProcessType) {
        this.extractionProcessType = extractionProcessType;
    }

    public String getMicroserviceFieldConfig() {
        return microserviceFieldConfig;
    }

    public void setMicroserviceFieldConfig(String microserviceFieldConfig) {
        this.microserviceFieldConfig = microserviceFieldConfig;
    }
}
