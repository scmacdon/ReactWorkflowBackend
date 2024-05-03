// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.Field;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultRequest;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultResponse;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import software.amazon.awssdk.services.redshiftdata.model.SqlParameter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkflowService {

    final String clusterId = "redshift-cluster-wf" ;
    final String databaseName = "dev" ;

    private RedshiftDataClient getDataClient() {
        // Replace with your AWS access key ID and secret access key
        String accessKeyId = "<enter key>";
        String secretAccessKey = "<enter key>";

        // Create AWS credentials object
        AwsCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        // Create a static credentials provider with the custom credentials
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        Region region = Region.US_EAST_1;
        return RedshiftDataClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(region)
            .build();
    }

    public String countItems() {
        String sqlStatement = "SELECT COUNT(*) AS RecordCount FROM Workflow where Status = 'Done';";

        ExecuteStatementRequest executeStatementRequest = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(executeStatementRequest);
        String id = response.id();
        checkStatement(id);
        long recordCount = getCount(id);
        return String.valueOf(recordCount);
    }

    public String popTable(Details detailsOb, Boolean isScout) {
        // Use SqlParameter to avoid SQL injection.
        List<SqlParameter> parameterList = new ArrayList<>();
        UUID uuid = UUID.randomUUID();
        int intValue = Math.abs(uuid.hashCode());
        String sqlStatement = "INSERT INTO Workflow (id, title, engineer, summary, service, type, sme, language, guide, url, sos, status, score, date) VALUES (:id, :title, :engineer, :summary, :service, :type, :sme, :language, :guide, :serviceURL, :sos, :status, :score, :date);";

        // Create the parameters.
        SqlParameter idParam = SqlParameter.builder()
            .name("id")
            .value(String.valueOf(intValue))
            .build();

        SqlParameter idTitle = SqlParameter.builder()
            .name("title")
            .value(detailsOb.getTitle())
            .build();

        SqlParameter idEngineer = SqlParameter.builder()
            .name("engineer")
            .value(detailsOb.getEngineer())
            .build();

        SqlParameter idSummary = SqlParameter.builder()
            .name("summary")
            .value(detailsOb.getSummary())
            .build();

        SqlParameter service1 = SqlParameter.builder()
            .name("service")
            .value(detailsOb.getService())
            .build();

        SqlParameter typeParam = null;
        if (isScout) {
            typeParam = SqlParameter.builder()
                .name("type")
                .value(detailsOb.getType())
                .build();
        } else {
            typeParam = SqlParameter.builder()
                .name("type")
                .value("Follow")
                .build();
        }

        SqlParameter idSme = SqlParameter.builder()
            .name("sme")
            .value(detailsOb.getSme())
            .build();

        SqlParameter idLanguage = SqlParameter.builder()
            .name("language")
            .value(detailsOb.getLanguage())
            .build();

        SqlParameter idGuide = SqlParameter.builder()
            .name("guide")
            .value(detailsOb.getGuide())
            .build();

        SqlParameter idServiceURL = SqlParameter.builder()
            .name("serviceURL")
            .value(detailsOb.getUrl())
            .build();

        SqlParameter idSos = SqlParameter.builder()
            .name("sos")
            .value(String.valueOf(detailsOb.getSos()))
            .build();

        SqlParameter idStatus;
        if (isScout) {
            idStatus = SqlParameter.builder()
                .name("status")
                .value("Draft")
                .build();
        } else {
            idStatus = SqlParameter.builder()
                .name("status")
                .value("Research")
                .build();
        }

        SqlParameter idScore;
        if (isScout) {
            idScore = SqlParameter.builder()
                .name("score")
                .value("0")
                .build();
        } else {
            idScore = SqlParameter.builder()
                .name("score")
                .value("50")
                .build();
        }

        // Get date value for today.
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = today.format(formatter);

        SqlParameter paramDate = SqlParameter.builder()
            .name("date")
            .value(formattedDate)
            .build();

        parameterList.add(idParam);
        parameterList.add(idTitle);
        parameterList.add(idEngineer);
        parameterList.add(idSummary);
        parameterList.add(service1);
        parameterList.add(typeParam);
        parameterList.add(idSme);
        parameterList.add(idLanguage);
        parameterList.add(idGuide);
        parameterList.add(idServiceURL);
        parameterList.add(idSos);
        parameterList.add(idStatus);
        parameterList.add(idScore);
        parameterList.add(paramDate);

        try {
            ExecuteStatementRequest insertStatementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .sql(sqlStatement)
                .database(databaseName)
                .dbUser("awsuser")
                .parameters(parameterList)
                .build();

            ExecuteStatementResponse response = getDataClient().executeStatement(insertStatementRequest);
            String id = response.id();
            checkStatement(id);
            System.out.println("Inserted new record: " );
            return String.valueOf(intValue);

        } catch (RedshiftDataException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            System.exit(1);
        }
        return "" ;
    }


    // This method updates an existing item.
    public String updateItem(Details detailsOb) {
        // Use SqlParameter to avoid SQL injection.
        List<SqlParameter> parameterList = new ArrayList<>();
        String sqlStatement = "UPDATE Workflow SET title = :title, engineer = :engineer, summary = :summary, service = :service, sme = :sme, language = :language, guide = :guide, url = :url  WHERE id = "+detailsOb.getId()+";";

        SqlParameter paramTitle = SqlParameter.builder()
            .name("title")
            .value(detailsOb.getTitle())
            .build();

        SqlParameter paramEngineer = SqlParameter.builder()
            .name("engineer")
            .value(detailsOb.getEngineer())
            .build();

        SqlParameter paramSummary = SqlParameter.builder()
            .name("summary")
            .value(detailsOb.getSummary())
            .build();

        SqlParameter paramService = SqlParameter.builder()
            .name("service")
            .value(detailsOb.getService())
            .build();

        SqlParameter paramSme = SqlParameter.builder()
            .name("sme")
            .value(detailsOb.getSme())
            .build();

        SqlParameter paramLanguage = SqlParameter.builder()
            .name("language")
            .value(detailsOb.getLanguage())
            .build();

        SqlParameter paramGuide = SqlParameter.builder()
            .name("guide")
            .value(detailsOb.getGuide())
            .build();

        SqlParameter paramServiceURL = SqlParameter.builder()
            .name("url")
            .value(detailsOb.getUrl())
            .build();


        parameterList.add(paramTitle);
        parameterList.add(paramEngineer);
        parameterList.add(paramSummary);
        parameterList.add(paramService);
        parameterList.add(paramSme);
        parameterList.add(paramLanguage);
        parameterList.add(paramGuide);
        parameterList.add(paramServiceURL);
        try {
            ExecuteStatementRequest insertStatementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .sql(sqlStatement)
                .database(databaseName)
                .dbUser("awsuser")
                .parameters(parameterList)
                .build();

            getDataClient().executeStatement(insertStatementRequest);
            return "Successfully updated item " +detailsOb.getId();

        } catch (RedshiftDataException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            System.exit(1);
        }
        return "" ;
    }

    // UPDATE Workflow SET status = 'Approve' WHERE id = 322990198;
    public String modItem(String id, String status) {
        String sqlStatement = "update Workflow set status = :status where id =:id ";
        SqlParameter paraStatus = SqlParameter.builder()
            .name("status")
            .value(status)
            .build();

        SqlParameter paraId = SqlParameter.builder()
            .name("id")
            .value(id)
            .build();

        List<SqlParameter> parameters = List.of(
            paraStatus,
            paraId
        );

        try {
            ExecuteStatementRequest insertStatementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .sql(sqlStatement)
                .dbUser("awsuser")
                .database(databaseName)
                .parameters(parameters)
                .build();

            getDataClient().executeStatement(insertStatementRequest);

        } catch (RedshiftDataException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            System.exit(1);
        }
        return "" ;
    }


    public List<Details> getData(String status) {
        String sqlStatement ="";
        if (status.compareTo("Draft") == 0) {
            sqlStatement = "SELECT id, title, engineer, type, service, sme, language, status FROM Workflow where Status = 'Draft';";
        } else if (status.compareTo("Approved") == 0) {
            sqlStatement = "SELECT id, title, engineer, type, service, sme, language, status FROM Workflow WHERE status IN ('Approved', 'Research', 'InProgress');";
        } else if (status.compareTo("Research") == 0) {
            sqlStatement = "SELECT id, title, engineer, type, service, sme, language, status FROM Workflow where Status = 'Research';";
        }

        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .database(databaseName)
            .dbUser("awsuser")
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String id = response.id();
        System.out.println("The identifier of the statement is " + id);
        checkStatement(id);
        return getResultWithStatus(id);
    }

    public List<Details> getDataByIdWithType(Long pk) {
        String sqlStatement = "SELECT id, title, engineer, summary, service, sme, language, status, type FROM Workflow where Id = " +pk +";";
        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String id = response.id();
        System.out.println("The identifier of the statement is " + id);
        checkStatement(id);
        return getResultsType(id);
    }

    public List<Details> getDataById(Long pk) {
        String sqlStatement = "SELECT id, title, engineer, summary, service, sme, language, guide, url FROM Workflow where Id = " +pk +";";
        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String id = response.id();
        System.out.println("The identifier of the statement is " + id);
        checkStatement(id);
        return getResults(id);
    }

    public List<Details> getDataByIdForUpdate(Long pk) {
        String sqlStatement = "SELECT id, title, engineer, summary,service, service2, sme, language, guide, url FROM Workflow where Id = " +pk +";";
        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String id = response.id();
        System.out.println("The identifier of the statement is " + id);
        checkStatement(id);
        return getResults(id);
    }


    void checkStatement(String sqlId) {
        try {
            DescribeStatementRequest statementRequest = DescribeStatementRequest.builder()
                .id(sqlId)
                .build();

            // Wait until the SQL statement processing is finished.
            String status;
            while (true) {
                DescribeStatementResponse response = getDataClient().describeStatement(statementRequest);
                status = response.statusAsString();
                System.out.println("Statement status: " + status);

                if (status.equals("FINISHED")) {
                    System.out.println("The statement is finished!");
                    break;
                } else if (status.equals("FAILED")) {
                    // If the statement failed, fetch additional error information
                    String error = response.error();
                    if (error != null) {
                        System.err.println("Statement failed with error:");
                        System.err.println("  ERROR Code: " + error);

                    } else {
                        System.err.println("Statement failed but no error information available.");
                    }
                    break;
                }
                Thread.sleep(500);
            }

        } catch (RedshiftDataException e) {
            System.err.println("Redshift Data Exception occurred: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("InterruptedException occurred: " + e.getMessage());
            e.printStackTrace();
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }

    List<Details> getResults(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);
            return response
                .records()
                .stream()
                .map(Details::from)
                .collect(Collectors.toUnmodifiableList());

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    List<Details> getResultWithStatus(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);
            return response
                .records()
                .stream()
                .map(Details::fromWithStatus)
                .collect(Collectors.toUnmodifiableList());

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public long getCount(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);

            // Check if there are records in the result set
            if (!response.records().isEmpty()) {
                // Assuming the count is in the first column of the first row
                return response.records().get(0).get(0).longValue();
            } else {
                // Return 0 if there are no records
                return 0;
            }

        } catch (RedshiftDataException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            // You might want to return an error value or throw an exception here
            return 0;
        }
    }

    // Update the status of an item from Research to Inprogress.
    public String modResearchItem(String id, String status) {

        String sqlStatement = "update Workflow set status = :status where id =:id ";
        SqlParameter paraStatus = SqlParameter.builder()
            .name("status")
            .value(status)
            .build();

        SqlParameter paraId = SqlParameter.builder()
            .name("id")
            .value(id)
            .build();

        List<SqlParameter> parameters = List.of(
            paraStatus,
            paraId
        );

        try {
            ExecuteStatementRequest insertStatementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .sql(sqlStatement)
                .database(databaseName)
                .parameters(parameters)
                .build();

            getDataClient().executeStatement(insertStatementRequest);
            System.out.println("Updated record " );
            return String.valueOf("Updated record "+id);

        } catch (RedshiftDataException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            System.exit(1);
        }
        return "" ;
    }

    public int lookUpItemScore(String id) {
        int intId = Integer.parseInt(id);
        String sqlStatement = "SELECT score FROM Workflow WHERE id = " + intId + ";";

        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String sqlId = response.id();
        checkStatement(sqlId);
        return getItemScore(sqlId);
    }

    public int getItemScore(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);

            // Check if there are records in the result set
            if (!response.records().isEmpty()) {
                // Assuming the count is in the first column of the first row
                return Math.toIntExact(response.records().get(0).get(0).longValue());
            } else {
                // Return 0 if there are no records
                return 0;
            }

        } catch (RedshiftDataException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            // You might want to return an error value or throw an exception here
            return 0;
        }
    }

    public void updateScore(int score, String id) {
        String sqlStatement = "UPDATE Workflow SET score = :score where id =:id ";

        SqlParameter paraScore = SqlParameter.builder()
            .name("score")
            .value(String.valueOf(score))
            .build();

        SqlParameter paraId = SqlParameter.builder()
            .name("id")
            .value(id)
            .build();

        try {
            List<SqlParameter> parameters = List.of(
                paraScore,
                paraId);

            ExecuteStatementRequest insertStatementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .sql(sqlStatement)
                .database(databaseName)
                .parameters(parameters)
                .build();

            ExecuteStatementResponse response = getDataClient().executeStatement(insertStatementRequest);
            String sqlId = response.id();
            checkStatement(sqlId);
            System.out.println("Updated record " );

        } catch (RedshiftDataException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            System.exit(1);
        }
    }

    public List<Details> getDataByIdWithScore(Long pk) {
        String sqlStatement = "SELECT id, url, engineer, summary, service, sme, language, guide, score FROM Workflow where Id = " +pk +";";
        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String id = response.id();
        System.out.println("The identifier of the statement is " + id);
        checkStatement(id);
        return getResultsScore(id);
    }

    List<Details> getResultsScore(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);
            return response
                .records()
                .stream()
                .map(Details::fromWithScore)
                .collect(Collectors.toUnmodifiableList());

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    List<Details> getResultsType(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);
            return response
                .records()
                .stream()
                .map(Details::fromWithType)
                .collect(Collectors.toUnmodifiableList());

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    // Required for the Complete view in the app.
    public List<CompleteDetails> getCompleteDetails() {
        String sqlStatement = "SELECT Engineer, Type from Workflow WHERE Status = 'Done';";
        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .dbUser("awsuser")
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String sqlId = response.id();
        checkStatement(sqlId);
        return getResultComplete(sqlId);
    }

    public List<CompleteDetails> getResultComplete(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);
            return response.records().stream()
                .map(CompleteDetails::from)
                .collect(Collectors.toList());

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    // Need to get Done items by Eng name
    // Item Id	Status	Title	Engineer	Type	Service
    public List<Details> getDoneItemsByEng(String engName) {
        String sqlStatement = "SELECT id, status, title, engineer, type, service FROM Workflow WHERE engineer = '" + engName + "' AND status = 'Done';";

        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .dbUser("awsuser")
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String id = response.id();
        System.out.println("The identifier of the statement is " + id);
        checkStatement(id);
        return getResultDone(id);


    }

    // Handles the Result Set for items that are done.
    List<Details> getResultDone(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);
            return response
                .records()
                .stream()
                .map(Details::fromWithDone)
                .collect(Collectors.toUnmodifiableList());

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    // Need to look up an item to ensure item is a draft.
    // Returns true if item is in Draft - else false.
    public boolean lookupItem(String itemId) {
        String sqlStatement = "SELECT status FROM Workflow WHERE id = " + itemId +";";

        ExecuteStatementRequest req = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .dbUser("awsuser")
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(req);
        String id = response.id();
        System.out.println("The identifier of the statement is " + id);
        checkStatement(id);
        return isDraftResult(id);


    }

    private boolean isDraftResult(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);

            // Extract status from the response
            List<List<Field>> records = response.records();
            if (records.size() > 0) {
                List<Field> fields = records.get(0);
                if (fields.size() > 0) {
                    String status = fields.get(0).stringValue();
                    // Check if status is "Draft" and return true
                    return "Draft".equalsIgnoreCase(status);
                }
            }

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
             }
        // If no records were returned or status is not "Draft", return false
        return false;

    }



}
