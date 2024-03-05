package aws.workflowapp.redshift;

import org.springframework.stereotype.Component;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WorkItemRepository {

    final String  clusterId = "redshift-cluster-wf" ;
    final String databaseName = "dev" ;

    private RedshiftDataClient getDataClient() {
        Region region = Region.US_EAST_1;
        return RedshiftDataClient.builder()
            .region(region)
            .build();
    }

    public String countItems() {
        String sqlStatement = "SELECT COUNT(*) AS RecordCount FROM Workflow;";

        ExecuteStatementRequest executeStatementRequest = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .sql(sqlStatement)
            .database(databaseName)
            .build();

        ExecuteStatementResponse response = getDataClient().executeStatement(executeStatementRequest);
        String id = response.id();
        checkStatement(id);
        long recordCount =  getCount(id);
        return String.valueOf(recordCount);
    }

    public String popTable(Scout scoutOb) {
        // Use SqlParameter to avoid SQL injection.
        List<SqlParameter> parameterList = new ArrayList<>();
        String sqlStatement = "INSERT INTO Workflow VALUES( :id , :title, :engineer, :summary, :service, :addServices, :sme, :language, :guide, :serviceURL, :sos, :status, :score);";
        UUID uuid = UUID.randomUUID();
        int intValue = Math.abs(uuid.hashCode());
        // Create the parameters.
        SqlParameter idParam = SqlParameter.builder()
            .name("id")
            .value(String.valueOf(intValue))
            .build();

        SqlParameter idTitle = SqlParameter.builder()
            .name("title")
            .value(scoutOb.getTitle())
            .build();

        SqlParameter idEngineer = SqlParameter.builder()
            .name("engineer")
            .value(scoutOb.getEngineer())
            .build();

        SqlParameter idSummary = SqlParameter.builder()
            .name("summary")
            .value(scoutOb.getSummary())
            .build();

        SqlParameter idService = SqlParameter.builder()
            .name("service")
            .value(scoutOb.getService())
            .build();

        SqlParameter addServicesParam = SqlParameter.builder()
            .name("addServices")
            .value(scoutOb.getService2())
            .build();

        SqlParameter idSme = SqlParameter.builder()
            .name("sme")
            .value(scoutOb.getSme())
            .build();

        SqlParameter idLanguage = SqlParameter.builder()
            .name("language")
            .value(scoutOb.getLanguage())
            .build();

        SqlParameter idGuide = SqlParameter.builder()
            .name("guide")
            .value(scoutOb.getGuide())
            .build();

        SqlParameter idServiceURL = SqlParameter.builder()
            .name("serviceURL")
            .value(scoutOb.getUrl())
            .build();

        SqlParameter idSos = SqlParameter.builder()
            .name("sos")
            .value(String.valueOf(scoutOb.isIncludedInSOS()))
            .build();

        SqlParameter idStatus = SqlParameter.builder()
            .name("status")
            .value("Draft")
            .build();

        SqlParameter idScore = SqlParameter.builder()
            .name("score")
            .value("0")
            .build();

        parameterList.add(idParam);
        parameterList.add(idTitle);
        parameterList.add(idEngineer);
        parameterList.add(idSummary);
        parameterList.add(idService);
        parameterList.add(addServicesParam);
        parameterList.add(idSme);
        parameterList.add(idLanguage);
        parameterList.add(idGuide);
        parameterList.add(idServiceURL);
        parameterList.add(idSos);
        parameterList.add(idStatus);
        parameterList.add(idScore);

        try {
            ExecuteStatementRequest insertStatementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .sql(sqlStatement)
                .database(databaseName)
                .parameters(parameterList)
                .build();

            getDataClient().executeStatement(insertStatementRequest);
            System.out.println("Inserted new record: " );
            return String.valueOf(intValue);

        } catch (RedshiftDataException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            System.exit(1);
        }
        return "" ;
    }

    //UPDATE Workflow SET status = 'Approve' WHERE id = 322990198;
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


    // Return items from the work table.
    public List<Scout> getData(String status) {
        String sqlStatement ="";
        if (status.compareTo("Draft") == 0) {
            sqlStatement = "SELECT id, title, engineer, summary,service, sme, language, guide, url, status FROM Workflow where Status = 'Draft';";
        } else if (status.compareTo("Approved") == 0) {
            sqlStatement = "SELECT id, title, engineer, summary,service, sme, language, guide, url, status FROM Workflow WHERE status IN ('Approved', 'Research', 'InProgress');";
        } else if (status.compareTo("Research") == 0) {
            sqlStatement = "SELECT id, title, engineer, summary,service, sme, language, guide, url, status FROM Workflow where Status = 'Research';";
        }

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

    public List<Scout> getDataById(Long pk) {
        String sqlStatement = "SELECT id, title, engineer, summary,service, sme, language, guide, url, status FROM Workflow where Id = " +pk +";";
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

    void checkStatement(String sqlId ) {
        try {
            DescribeStatementRequest statementRequest = DescribeStatementRequest.builder()
                .id(sqlId)
                .build() ;

            // Wait until the sql statement processing is finished.
            String status;
            while (true) {
                DescribeStatementResponse response = getDataClient().describeStatement(statementRequest);
                status = response.statusAsString();
                System.out.println("..."+status);

                if (status.compareTo("FINISHED") == 0) {
                    break;
                }
                Thread.sleep(500);
            }
            System.out.println("The statement is finished!");

        } catch (RedshiftDataException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    List<Scout> getResults(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getDataClient().getStatementResult(resultRequest);
            return response
                .records()
                .stream()
                .map(Scout::from)
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
        int intId = Integer.parseInt(id);  // Convert id to int
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
}
