package com.map.mutual.side.review.controller;

import com.map.mutual.side.common.dto.ResponseJsonObject;
import com.map.mutual.side.common.enumerate.ApiStatusCode;
import com.map.mutual.side.common.enumerate.BooleanType;
import com.map.mutual.side.common.exception.YOPLEServiceException;
import com.map.mutual.side.common.utils.YOPLEUtils;
import com.map.mutual.side.review.model.dto.*;
import com.map.mutual.side.review.model.entity.EmojiEntity;
import com.map.mutual.side.review.model.entity.PlaceEntity;
import com.map.mutual.side.review.model.enumeration.EmojiType;
import com.map.mutual.side.review.repository.EmojiRepo;
import com.map.mutual.side.review.repository.PlaceRepo;
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
    private PlaceRepo placeRepo;
    @Autowired
    private EmojiRepo emojiRepo;


    /**
     * Review 생성
     * @param dto
     * ReviewPlaceDto:
     * { reviewDto: {}, placeDto: {} }
     *
     * reviewDto: {
     *      String content: 내용
     *      MultipartFile[] imageFiles: 리뷰에 올릴 이미지들
     *      List[Long] worldList: 월드 리스트
     * }
     * placeDto: {
     *     String placeId: place 고유번호
     *     String name: place 이름
     *     String address: place 주소
     *     String roadAddress: place 도로명
     *     String categoryGroupCode: 카테고리 그룹 코드
     *     String categoryGroupName: 카테고리 그룹 이름
     *     BigDecimal x: x좌표
     *     BigDecimal y: y좌표
     * }
     * // TODO: 2022/04/01 imageFiles 서버 구축 후 추가 테스트
     * @return
     */
    @PostMapping("/review")
    public ResponseEntity<ResponseJsonObject> createReview(@Valid @RequestBody ReviewPlaceDto dto) throws Exception {
        try {
            if (dto.getReview().getWorldList() == null || dto.getReview().getWorldList().isEmpty()) {
                throw new YOPLEServiceException(ApiStatusCode.WORLD_LIST_IS_NULL);
            }
            if (dto.getPlace() != null && !placeRepo.findById(dto.getPlace().getPlaceId()).isPresent()) {
                PlaceEntity placeEntity = PlaceEntity.builder()
                        .placeId(dto.getPlace().getPlaceId())
                        .name(dto.getPlace().getName())
                        .address(dto.getPlace().getAddress())
                        .roadAddress(dto.getPlace().getRoadAddress())
                        .categoryGroupCode(dto.getPlace().getCategoryGroupCode())
                        .categoryGroupName(dto.getPlace().getCategoryGroupName())
                        .x(dto.getPlace().getX())
                        .y(dto.getPlace().getY())
                        .build();

                placeRepo.save(placeEntity);
            }
            reviewService.createReview(dto);
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }
    /**
     * Review 수정
     * @param reviewDto
     * String content: 내용
     * Long reviewId: 수정할 리뷰 ID
     * MultipartFile[] imageFiles: 리뷰에 올릴 이미지들
     * List[Long] worldList: 월드 리스트
     * // TODO: 2022/04/01 imageFiles 서버 구축 후 추가 테스트
     * @return
     */
    @PutMapping("/review")
    public ResponseEntity<ResponseJsonObject> updateReview(@RequestBody ReviewDto reviewDto) {
        try {
            if (reviewDto.getWorldList() == null || reviewDto.getWorldList().isEmpty()) {
                throw new YOPLEServiceException(ApiStatusCode.WORLD_LIST_IS_NULL);
            } else  reviewService.updateReview(reviewDto);
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

    /**
     * Review 삭제
     * @param reviewId
     * Long reviewId: review id
     * @return
     * 해당 메소드는 리뷰뿐만 아니라, ReviewWorldMapping 데이터도 삭제합니다.
     */
    @DeleteMapping("/review")
    public ResponseEntity<ResponseJsonObject> deleteReview(@NotNull @RequestParam Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

    /**
     * Review 조회
     * @param reviewId
     * @return
     */
    @GetMapping("/review")
    public ResponseEntity<ResponseJsonObject> getReview(@RequestParam Long reviewId) {
        ResponseJsonObject responseJsonObject = new ResponseJsonObject();

        try {
            ReviewDto reviewDto = reviewService.getReview(reviewId);
            responseJsonObject.setData(reviewDto);

        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    /**
     * Review List 조회
     * @param worldId
     * @return
     * 월드 id에 해당하는 Review List들 조회
     */
    @GetMapping("/reviews")
    public ResponseEntity<ResponseJsonObject> getReviews(@RequestParam Long worldId) {
        ResponseJsonObject responseJsonObject;

        try {
            List<ReviewDto> reviewDto = reviewService.getReviews(worldId);
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(reviewDto);

        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    /**
     * 내가 쓴 Reviews 조회
     * @return
     * 내가 작성한 리뷰들을 불러옵니다.
     *
     */
    @GetMapping("/myReviews")
    public ResponseEntity<ResponseJsonObject> myReviews() {
        ResponseJsonObject responseJsonObject;

        try {
            List<ReviewDto> reviewDto = reviewService.myReviews();
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(reviewDto);
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    @GetMapping("/worldPin/review")
    public ResponseEntity<ResponseJsonObject> worldPinReview(@RequestParam Long worldId) {
        ResponseJsonObject responseJsonObject;

        try {
            List<ReviewDto> reviewDto = reviewService.worldPinReview(worldId);
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(reviewDto);
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    /**
     * x축, y축 범위에 따른 장소들 리스트를 가져옴.
     * @param placeRangeDto
     * @return
     */
    @GetMapping("/worldPin/place")
    public ResponseEntity<ResponseJsonObject> worldPinPlace(@RequestBody PlaceRangeDto placeRangeDto) {
        ResponseJsonObject responseJsonObject;

        try {
            List<PlaceDto.PlaceInRange> places = reviewService.worldPinPlace(placeRangeDto);
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(places);
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    /**
     * 선택한 월드, 장소값에 따라서
     * 장소의 리뷰들, 장소의 정보 가져옴.
     * @param placeId
     * @param worldId
     * @return
     */
    @GetMapping("/placeDetail")
    public ResponseEntity<ResponseJsonObject> placeDetail(@RequestParam String placeId, @RequestParam Long worldId) {
        ResponseJsonObject responseJsonObject;

        try {
            PlaceDetailDto result = reviewService.placeDetail(placeId, worldId);
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
            responseJsonObject.setData(result);
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }

    @PostMapping("/emoji")
    public ResponseEntity<ResponseJsonObject> addEmoji(@RequestParam Long reviewId, @RequestParam Long worldId, @RequestParam EmojiType emojiType) {
        ResponseJsonObject responseJsonObject;
        try {
            reviewService.addEmoji(reviewId, worldId, emojiType);
            responseJsonObject = ResponseJsonObject.withStatusCode(ApiStatusCode.OK);
//            responseJsonObject.setData(result);
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(responseJsonObject, HttpStatus.OK);
    }


    /**
     * TEST
     */
    @PostMapping("/emojiAdd")
    public void emojiAddTest() {
        ResponseJsonObject responseJsonObject;
        try {
            emojiRepo.save(EmojiEntity.builder()
                            .emojiStatus(BooleanType.Y)
                            .emojiImg(null)
                            .emojiValue(EmojiType.GOOD)
                    .build());
        } catch (YOPLEServiceException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }



    /**
     * TEST
     */
    @PostMapping("/upload")
    public ResponseEntity<ResponseJsonObject> upload(@RequestPart MultipartFile file, @RequestParam String tempReview) throws IOException {

        try {
            System.out.println("파일 이름 : " + file.getOriginalFilename());
            System.out.println("파일 크기 : " + file.getSize());

            String dirPath = File.separator
                    +"tmp"
                    +File.separator
                    +"yople"
                    +File.separator
                    +tempReview;

            YOPLEUtils.createDirectories(dirPath);


            FileOutputStream fos = new FileOutputStream(dirPath
                    +File.separator
                    +file.getOriginalFilename());

            InputStream is  = file.getInputStream();


            int readCount = 0;
            byte[] buffer = new byte[1024];

            while ((readCount = is.read(buffer)) != -1) {
                //  파일에서 가져온 fileInputStream을 설정한 크기 (1024byte) 만큼 읽고

                fos.write(buffer, 0, readCount);
                // 위에서 생성한 fileOutputStream 객체에 출력하기를 반복한다
            }

        } catch (YOPLEServiceException e) {
            throw e;
        }
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK), HttpStatus.OK);
    }

}
