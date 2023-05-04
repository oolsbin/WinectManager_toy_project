package com.example.demo.exception;

import lombok.Getter;

@Getter
public enum EErrorCode {

  // 공통 (common - 400)
  ERROR_WC001("ERROR_WC001", "예상치 못한 오류가 발생했습니다."),
  ERROR_WC002("ERROR_WC002", "예상치 못한 오류가 발생했습니다. 재로그인하세요."),

  // NOT FOUND 관련(not found - 404)
  ERROR_WN001("ERROR_WN001", "USER NOT FOUND"),
  ERROR_WN002("ERROR_WN002", "CONSUMER NOT FOUND"),
  ERROR_WN003("ERROR_WN003", "API NOT FOUND"),
  ERROR_WN004("ERROR_WN004", "API APPLY NOT FOUND"),
  ERROR_WN005("ERROR_WN005", "CATEGORY TYPE NOT FOUND"),
  ERROR_WN006("ERROR_WN006", "도메인이 존재하지 않습니다."),

  // 저장시 중복관련 (save - 409)
  ERROR_WS001("ERROR_WS001", "사용중인 아이디입니다."),
  ERROR_WS002("ERROR_WS002", "사용중인 권한명입니다."),
  ERROR_WS003("ERROR_WS003", "사용중인 이름입니다."),
  ERROR_WS004("ERROR_WS004", "사용중인 도메인명입니다."),
  ERROR_WS005("ERROR_WS005", "사용중인 카테고리명입니다."),

  // 저장시 오류관련 (save - 400)
  ERROR_WS101("ERROR_WS101", "저장불가 단어가 포함되어 있습니다."),
  ERROR_WS102("ERROR_WS102", "Gateway 또는 Mediation 사용 여부를 한 항목 이상 선택하세요."),
  ERROR_WS103("ERROR_WS103", "승인 대기중 또는 승인 완료된 API가 존재합니다."),
  ERROR_WS104("ERROR_WS104", "존재 하지 않는 도메인입니다. 새로고침 후 확인해주세요."),

  // 삭제시 사용관련 (remove - 400)
  ERROR_WR001("ERROR_WR001", "관리중인 도메인이 있습니다. 관리중인 도메인 삭제 후 재시도해주세요."),
  ERROR_WR002("ERROR_WR002", "권한을 사용중인 사용자가 있습니다. 사용자 권한 삭제 후 재시도해주세요."),
  ERROR_WR003("ERROR_WR003", "사용중인 서버는 삭제가 되지 않습니다. 서버를 종료 후 재시도해주세요."),

  // 권한관련 (auth - 401)
  ERROR_WA001("ERROR_WA001", "아이디 또는 비밀번호가 일치하지 않습니다."),
  ERROR_WA002("ERROR_WA002", "비밀번호가 일치하지 않습니다."),
  ERROR_WA003("ERROR_WA003", "권한이 없습니다."),
  ERROR_WA004("ERROR_WA004", "계정이 비활성화되었습니다. 관리자에게 문의하세요."),
  ERROR_WA005("ERROR_WA005", "비밀번호 유효기간이 만료 되었습니다. 관리자에게 문의하세요."),
  ERROR_WA006("ERROR_WA006", "접근 권한이 없습니다."),
  ERROR_WA007("ERROR_WA007", "유효하지 않은 접근입니다."),
  ERROR_WA008("ERROR_WA008", "계정이 잠겨있습니다. 관리자에게 문의하세요."),
  ERROR_WA009("ERROR_WA009", "다른곳에서 로그인이 되어 자동으로 로그아웃합니다."),

  // 토큰관련 (token - 401)
  ERROR_WT001("ERROR_WT001", "Incorrect signature"),
  ERROR_WT002("ERROR_WT002", "Malformed jwt token"),
  ERROR_WT003("ERROR_WT003", "Token expired"),
  ERROR_WT004("ERROR_WT004", "Unsupported JWT token"),
  ERROR_WT005("ERROR_WT005", "Illegal argument token"),

  // DB관련 (database - 500)
  ERROR_WD001("ERROR_WD001", "DB 오류입니다. 관리자에게 문의하세요."),
  ERROR_WD002("ERROR_WD002", "DB 오류입니다. 제약조건을 확인하세요."),

  // 파일 업로드관련 (file upload - 400)
  ERROR_WF001("ERROR_WF001", "파일 확장자가 일치하지 않습니다."),
  ERROR_WF002("ERROR_WF002", "업로드 파일에 오류가 있습니다."),
  ERROR_WF003("ERROR_WF003", "Boolean 형식의 경우 기본값은 필수입니다."),
  ERROR_WF004("ERROR_WF004", "업로드 경로를 확인할 수 없습니다. 관리자에게 문의하세요."),
  ERROR_WF005("ERROR_WF005", "빈 파일은 저장할 수 없습니다."),
  ERROR_WF006("ERROR_WF006", "현재 디렉토리 외부에 상대 경로가있는 파일을 저장할 수 없습니다."),
  ERROR_WF007("ERROR_WF007", "파일 업로드를 실패했습니다. 관리자에게 문의하세요."),

  // 파일 다운로드관련 (file download - 400)
  ERROR_WF101("ERROR_WF101", "파일을 다운로드 할 수 없습니다. 관리자에게 문의하세요."),

  // 기타 (400)
  ERROR_WT101("ERROR_WT101", "실행 불가능한 코드입니다.")
  ;

  private String errorCode;
  private String msg;

  EErrorCode(String errorCode, String msg) {
    this.errorCode = errorCode;
    this.msg = msg;
  }

  public String setMsg(String msg) {
    return this.msg = msg;
  }

}