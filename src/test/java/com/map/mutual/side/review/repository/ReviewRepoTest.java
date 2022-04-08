package com.map.mutual.side.review.repository;

import com.map.mutual.side.auth.model.entity.UserEntity;
import com.map.mutual.side.common.utils.TestConfig;
import com.map.mutual.side.review.model.dto.ReviewDto;
import com.map.mutual.side.review.model.entity.ReviewEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;


/**
 * fileName       : ReviewRepositoryTest
 * author         : kimjaejung
 * createDate     : 2022/03/22
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/22        kimjaejung       최초 생성
 *
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class ReviewRepoTest {
    @Autowired
    private ReviewRepo reviewRepo;


    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;
    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    @DisplayName("Review 저장하기")
    void save()
    {
        //given
        ReviewDto reviewDto = ReviewDto.builder()
                .content("test Content")
//                .imageUrls(Arrays.asList("a","b","c"))
                .userSuid("test")
                .worldId(1L)
                .build();


         ReviewEntity reviewEntity = ReviewEntity.builder()
//                 .worldEntity(WorldEntity.builder().worldId(1L).build())
                 .userEntity(UserEntity.builder().suid("TEST_SUID").build())
                .content(reviewDto.getContent())
//                .imageUrl(reviewDto.getImageUrls().stream().map(String::toString).collect(Collectors.joining(",")))
                .build();

         log.info("entitiy : \n{}", reviewEntity);


        reviewRepo.save(reviewEntity);

        Assertions.assertEquals(reviewEntity.getContent(),"test Content");

    }


}
