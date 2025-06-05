package com.sprint.mission.discodeit.service.jcf;

public class ErrorMessageService {
    // 에러 메시지 출력
    public void printErrorMessage(String methodName){
        System.out.println("<" + methodName + " 실패: 잘못된 id 값입니다. 객체가 null이거나 활동 가능한 상태가 아닙니다.>");
    }
}
