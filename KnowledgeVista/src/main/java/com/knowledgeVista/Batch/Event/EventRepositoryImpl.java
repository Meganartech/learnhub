//package com.knowledgeVista.Batch.Event;
//
//import java.util.List;
//
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//
//@Repository
//@Transactional
//public class EventRepositoryImpl implements EventRepository {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public List<EventDTO> getEventsByBatchIds(List<Long> batchIds ,int pageNumber, int pageSize) {
//        int offset = (pageNumber - 1) * pageSize;
//
//        String sql = """
//            SELECT * FROM (
//                SELECT 
//                    m.topic AS title, 
//                    m.meeting_id AS MeetingId, 
//                    NULL AS startDateTime, 
//                    NULL AS endDateTime,    
//                    m.duration AS duration, 
//                    m.start_time AS startTime, 
//                    b.batch_id AS batch,
//                    'MEET' AS type
//                FROM meeting m
//                JOIN meeting_batch_mapping mbm ON m.pk_id = mbm.meeting_id
//                JOIN batch b ON mbm.batch_id = b.id
//                WHERE b.id IN (:batchIds)
//
//                UNION ALL
//
//                SELECT 
//                    q.quizz_name AS title,
//                    NULL AS MeetingId,
//                    qs.start_date AS startDateTime,
//                    qs.end_date AS endDateTime,
//                    NULL AS duration,
//                    NULL AS startTime,
//                    b.batch_id AS batch,
//                    'QUIZZ' AS type
//                FROM quizz_schedule qs
//                JOIN batch b ON qs.batch_id = b.id
//                JOIN quizz q ON qs.quiz_id = q.quizz_id
//                WHERE b.id IN (:batchIds)
//            ) AS combined_results
//            ORDER BY startDateTime
//            LIMIT :pageSize OFFSET :offset
//        """;
//
//        Query query = entityManager.createNativeQuery(sql, "EventMapping");
//        query.setParameter("batchIds", batchIds);
//        query.setParameter("pageSize", pageSize);
//        query.setParameter("offset", offset);
//
//        return query.getResultList();
//    }
//}
//
//

package com.knowledgeVista.Batch.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
@Transactional
public class EventRepositoryImpl implements EventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> getEventsByBatchIds(List<Long> batchIds, int pageNumber, int pageSize) {
        int offset = (pageNumber) * pageSize;

        // ✅ Query to Get Paginated Data
        String sql = """
        	    SELECT * FROM (
        	        SELECT 
        	            m.topic AS title, 
        	            m.meeting_id AS MeetingId, 
        	            NULL AS QuizzDate,    
        	            m.duration AS duration, 
        	            TO_TIMESTAMP(m.start_time, 'YYYY-MM-DD"T"HH24:MI:SS') AS startTime,
        	            b.batch_id AS batchString,
        	            'MEET' AS type,
        	            b.id AS batchid,
        	            b.batch_title AS batchName,
        	            NULL AS quizzid
        	        FROM meeting m
        	        JOIN meeting_batch_mapping mbm ON m.pk_id = mbm.meeting_id
        	        JOIN batch b ON mbm.batch_id = b.id
        	        WHERE b.id IN (:batchIds)
        	         AND TO_DATE(m.start_time, 'YYYY-MM-DD') >= CURRENT_DATE -- ✅ Filter past meetings

        	        UNION ALL

        	        SELECT 
        	            q.quizz_name AS title,
        	            NULL AS MeetingId,
        	            CAST(qs.quizz_date AS TIMESTAMP) AS QuizzDate,
        	            q.duration_in_minutes AS duration,
        	            NULL AS startTime,
        	            b.batch_id AS batchString,
        	            'QUIZZ' AS type,
        	            b.id AS batchid,
        	            b.batch_title AS batchName,
        	            q.quizz_id AS quizzid
        	        FROM quizz_schedule qs
        	        JOIN batch b ON qs.batch_id = b.id
        	        JOIN quizz q ON qs.quiz_id = q.quizz_id
        	        WHERE b.id IN (:batchIds)
        	        AND qs.quizz_date >= CURRENT_DATE -- ✅ Filter past quizzes
        	    ) AS combined_results
        	    ORDER BY COALESCE(startTime, QuizzDate) 
        	    LIMIT :pageSize OFFSET :offset
        	""";


        Query query = entityManager.createNativeQuery(sql, "EventMapping");
        query.setParameter("batchIds", batchIds);
        query.setParameter("pageSize", pageSize);
        query.setParameter("offset", offset);

        List<EventDTO> events = query.getResultList();

        // ✅ Query to Get Total Count
        String countSql = """
            SELECT COUNT(*) FROM (
                SELECT m.topic FROM meeting m
                JOIN meeting_batch_mapping mbm ON m.pk_id = mbm.meeting_id
                JOIN batch b ON mbm.batch_id = b.id
                WHERE b.id IN (:batchIds)

                UNION ALL

                SELECT q.quizz_name FROM quizz_schedule qs
                JOIN batch b ON qs.batch_id = b.id
                JOIN quizz q ON qs.quiz_id = q.quizz_id
                WHERE b.id IN (:batchIds)
            ) AS total_results
        """;

        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("batchIds", batchIds);
        Number totalCount = (Number) countQuery.getSingleResult();

        // ✅ Return both events & total count
        Map<String, Object> response = new HashMap<>();
        response.put("events", events);
        response.put("totalCount", totalCount.intValue());

        return response;
    }
}

