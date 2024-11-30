package com.knowledgeVista.Migration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;

public class InsertQueryGenerator {
	
	@Autowired
	public MuserRepositories muser;

    public  <T> void generateInsertQueries(Class<T> clazz, List<T> entities) throws IllegalAccessException {
        StringBuilder insertQueryPrefix = new StringBuilder("INSERT INTO ");
        String tableName = clazz.getSimpleName().toLowerCase(); // Using class name as table name
        insertQueryPrefix.append(tableName).append(" (");
//        List<Muser> users = muser.findAll();


        Field[] fields = clazz.getDeclaredFields();
        StringBuilder columnNames = new StringBuilder();
        StringBuilder valuePlaceholders = new StringBuilder();

        // Generate column names and placeholders
        for (Field field : fields) {
            field.setAccessible(true); // Access private fields
            columnNames.append(field.getName()).append(", ");
            valuePlaceholders.append("?, ");
        }

        // Remove trailing ", "
        columnNames.setLength(columnNames.length() - 2);
        valuePlaceholders.setLength(valuePlaceholders.length() - 2);

        insertQueryPrefix.append(columnNames).append(") VALUES (").append(valuePlaceholders).append(");");

        // Process each entity and generate the query
        for (T entity : entities) {
            StringBuilder query = new StringBuilder(insertQueryPrefix.toString());

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(entity);

                // Replace placeholders with actual values
                if (value == null) {
                    query.replace(query.indexOf("?"), query.indexOf("?") + 1, "NULL");
                } else if (value instanceof String) {
                    query.replace(query.indexOf("?"), query.indexOf("?") + 1, "'" + value.toString().replace("'", "''") + "'");
                } else if (value instanceof Boolean) {
                    query.replace(query.indexOf("?"), query.indexOf("?") + 1, (Boolean.TRUE.equals(value) ? "TRUE" : "FALSE"));
                } else if (value instanceof Number) {
                    query.replace(query.indexOf("?"), query.indexOf("?") + 1, value.toString());
                } else if (value instanceof java.time.LocalDate || value instanceof java.time.LocalDateTime) {
                    query.replace(query.indexOf("?"), query.indexOf("?") + 1, "'" + value.toString() + "'");
                } else {
                    // For other objects, use their `toString()` or handle as necessary
                    query.replace(query.indexOf("?"), query.indexOf("?") + 1, "'" + value.toString() + "'");
                }
            }

            // Output the query
            System.out.println(query);
            
        }
    }
    
//        public static void main(String[] args) throws IllegalAccessException {
//            // Simulated findAll response
//            Muser user1 = new Muser();
//            user1.setUserId(1L);
//            user1.setUsername("SYSADMIN");
//            user1.setPsw("12345678");
//            user1.setEmail("sysadmin@gmail.com");
//            user1.setDob(java.time.LocalDate.of(1990, 1, 1));
//            user1.setPhone("1234567890");
//            user1.setSkills("Java,Spring");
//            user1.setInstitutionName("Meganartech");
//            user1.setCountryCode("+91");
//            user1.setIsActive(true);
//            user1.setLastactive(java.time.LocalDateTime.now());
//
//            Muser user2 = new Muser();
//            user2.setUserId(3L);
//            user2.setUsername("admin1");
//            user2.setPsw("Admin1@123");
//            user2.setEmail("admin1@gmail.com");
//            user2.setDob(java.time.LocalDate.of(2005, 6, 16));
//            user2.setPhone("9876545678");
//            user2.setSkills("");
//            user2.setInstitutionName("sample");
//            user2.setCountryCode("+91");
//            user2.setIsActive(true);
//
//            List<Muser> users = Arrays.asList(user1, user2);
//            
//
//            
//           
           
//        }
    

}

