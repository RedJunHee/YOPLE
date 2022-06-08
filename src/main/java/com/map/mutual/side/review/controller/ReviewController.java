package com.map.mutual.side.review.controller;

import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.utils.YOPLEUtils;
import com.map.mutual.side.review.model.dto.*;
import com.map.mutual.side.review.model.entity.EmojiEntity;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import com.map.mutual.side.review.repository.EmojiRepo;
import com.map.mutual.side.review.svc.ReviewService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * fileName       : ReivewController
 * author         : kimjaejung
 * createDate     : 2022/03/22
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/03/22        kimjaejung       최초 생성
 */
@RestController
@Log4j2
@Validated
@RequestMapping(value = "/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private EmojiRepo emojiRepo;


    /**
     * Review 생성
     *
     * @param dto ReviewPlaceDto:
     *            { reviewDto: {}, placeDto: {} }
     *            <p>
     *            reviewDto: {
     *            String content: 내용
     *            MultipartFile[] imageFiles: 리뷰에 올릴 이미지들
     *            List[Long] worldList: 월드 리스트
     *            }
     *            placeDto: {
     *            String placeId: place 고유번호
     *            String name: place 이름
     *            String address: place 주소
     *            String roadAddress: place 도로명
     *            String categoryGroupCode: 카테고리 그룹 코드
     *            String categoryGroupName: 카테고리 그룹 이름
     *            BigDecimal x: x좌표
     *            BigDecimal y: y좌표
     *            }
     * @return
     */
    @PostMapping("/review")
    public ResponseEntity<ResponseJsonObject> createReview(@Valid @RequestBody ReviewPlaceDto dto) throws Exception {
        if (dto.getReview().getWorldList() == null || dto.getReview().getWorldList().isEmpty()) {
            log.debug("Review 생성하기 - 리뷰에 매핑시킬 월드 리스트가 비어있음.");
            throw new YOPLEServiceException(ApiStatusCode.WORLD_LIST_IS_NULL);
        }

        reviewService.createReview(dto);
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

    /**
     * Review 수정
     *
     * @param reviewDto
     * @return
     */
    @PutMapping("/review")
    public ResponseEntity<ResponseJsonObject> updateReview(@RequestBody ReviewDto reviewDto) throws YOPLEServiceException {
        if (reviewDto.getWorldList() == null || reviewDto.getWorldList().isEmpty()) {
            log.debug("Review 생성하기 - 리뷰에 매핑시킬 월드 리스트가 비어있음.");
            throw new YOPLEServiceException(ApiStatusCode.WORLD_LIST_IS_NULL);
        } else reviewService.updateReview(reviewDto);
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

    /**
     * Review 삭제
     *
     * @param reviewId Long reviewId: review id
     * @return 해당 메소드는 리뷰뿐만 아니라, ReviewWorldMapping 데이터도 삭제합니다.
     */
    @DeleteMapping("/review")
    public ResponseEntity<ResponseJsonObject> deleteReview(@NotNull @RequestParam Long reviewId) throws YOPLEServiceException {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

    /**
     * Review 조회
     *
     * @param reviewId
     * @return
     */
    @GetMapping("/review")
    public ResponseEntity<ResponseJsonObject> getReview(@RequestParam Long reviewId, @RequestParam Long worldId) throws YOPLEServiceException {
        ResponseJsonObject responseJsonObject;

        ReviewDto.ReviewWithInviterDto result = reviewService.getReview(reviewId, worldId);
        responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
        responseJsonObject.setData(result);

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    @GetMapping("/pre/review")
    public ResponseEntity<ResponseJsonObject> getPreReview(@RequestParam Long reviewId) throws YOPLEServiceException {
        ResponseJsonObject responseJsonObject;

        ReviewDto.preReview result = reviewService.getPreReview(reviewId);
        responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
        responseJsonObject.setData(result);

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }


    /**
     * 내가 쓴 Reviews 조회
     *
     * @return 내가 작성한 리뷰들을 불러옵니다.
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<ResponseJsonObject> myReviews() throws YOPLEServiceException {
        ResponseJsonObject responseJsonObject;

        List<ReviewDto.MyReview> reviewDto = reviewService.myReviews();
        responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
        responseJsonObject.setData(reviewDto);
        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }


    /**
     * x축, y축 범위에 따른 장소들 리스트를 가져옴.
     *
     * @param worldId
     * @param x_start
     * @param x_end
     * @param y_start
     * @param y_end
     * @return
     * @throws YOPLEServiceException
     */
    @GetMapping("/world-pin")
    public ResponseEntity<ResponseJsonObject> worldPinPlaceInRange(@RequestParam Long worldId,
                                                                   @RequestParam BigDecimal x_start,
                                                                   @RequestParam BigDecimal x_end,
                                                                   @RequestParam BigDecimal y_start,
                                                                   @RequestParam BigDecimal y_end) throws YOPLEServiceException {
        ResponseJsonObject responseJsonObject;

        PlaceRangeDto placeRangeDto = PlaceRangeDto.builder()
                .worldId(worldId)
                .x_start(x_start)
                .x_end(x_end)
                .y_start(y_start)
                .y_end(y_end)
                .build();
        List<PlaceDto.PlaceSimpleDto> places = reviewService.worldPinPlaceInRange(placeRangeDto);
        responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
        responseJsonObject.setData(places);

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    /**
     * 선택한 월드, 장소값에 따라서
     * 장소의 간소화된 리뷰 리스트, 장소의 정보 가져옴.
     *
     * @param placeId
     * @param worldId
     * @return
     */
    @GetMapping("/place-detail")
    public ResponseEntity<ResponseJsonObject> placeDetail(@RequestParam String placeId, @RequestParam Long worldId) throws YOPLEServiceException {
        ResponseJsonObject responseJsonObject;

        PlaceDetailDto result = reviewService.placeDetail(placeId, worldId);
        responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
        responseJsonObject.setData(result);

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    /**
     * 이모지 추가
     *
     * @param reviewId
     * @param worldId
     * @param emojiId
     * @return
     */
    @PostMapping("/emoji")
    public ResponseEntity<ResponseJsonObject> addEmoji(@RequestParam Long reviewId, @RequestParam Long worldId, @RequestParam Long emojiId) throws YOPLEServiceException {
        ResponseJsonObject responseJsonObject;
        reviewService.addEmoji(reviewId, worldId, emojiId);
        responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }


    /**
     * TEST=====================================================================================================================================
     */
    @PostMapping("/emoji/update")
    public ResponseEntity<ResponseJsonObject> emojiUpdate() {
        List<EmojiEntity> emojiEntities = new ArrayList<>();
        Arrays.stream(EmojiType.values()).forEach(data -> emojiEntities.add(EmojiEntity.builder()
                .emojiId(data)
                .emojiStatus(data)
                .emojiImg(data)
                .emojiValue(data)
                .build()));
        emojiRepo.saveAll(emojiEntities);
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseJsonObject> upload(@RequestPart MultipartFile file, @RequestParam String tempReview) throws IOException {

        System.out.println("파일 이름 : " + file.getOriginalFilename());
        System.out.println("파일 크기 : " + file.getSize());

        String dirPath = File.separator
                + "tmp"
                + File.separator
                + "yople"
                + File.separator
                + tempReview;

        YOPLEUtils.createDirectories(dirPath);


        FileOutputStream fos = new FileOutputStream(dirPath
                + File.separator
                + file.getOriginalFilename());

        InputStream is = file.getInputStream();


        int readCount = 0;
        byte[] buffer = new byte[1024];

        while ((readCount = is.read(buffer)) != -1) {
            //  파일에서 가져온 fileInputStream을 설정한 크기 (1024byte) 만큼 읽고

            fos.write(buffer, 0, readCount);
            // 위에서 생성한 fileOutputStream 객체에 출력하기를 반복한다
        }

        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

}
