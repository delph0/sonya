CREATE OR REPLACE PROCEDURE sp_cdd_xset_match_customer
(
    p_src_system IN VARCHAR2,           -- 필수: 원천시스템(예. FMS, HOMEPAGE) 
    p_src_table IN VARCHAR2,            -- 필수: 원천고객테이블명(예. SUPERM, ACCOUNTM, TH_MEMBER_MASTER) 
    p_src_cust_id IN VARCHAR2,          -- 필수: 원천고객ID 
    p_superm_cust_id IN VARCHAR2,       -- 필수: 매칭후원자번호
    p_simularity IN NUMBER,             -- 유사도
    p_fix_yn IN VARCHAR2,               -- 고정유무
    p_rep_superm_cust_yn IN VARCHAR2,   -- 대표후원자유무
    p_remark IN VARCHAR2,               -- 비고
    p_manual_user_id IN VARCHAR2        -- 필수: 수작업 사용자ID    
)
IS
/******************************************************************************
   NAME:       sp_cdd_xset_match_customer
   PURPOSE:    

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        2008-09-25  배영규           1. Created this procedure.

   NOTES:

   Automatically available Auto Replace Keywords:
      Object Name:     sp_cdd_xset_match_customer
      Sysdate:         2008-09-25
      Date and Time:   2008-09-25, 오후 6:35:00, and 2008-09-25 오후 6:35:00
      Username:         (set in TOAD Options, Procedure Editor)
      Table Name:       (set in the "New PL/SQL Object" dialog)

******************************************************************************/
user_define_error1 EXCEPTION;
user_define_error2 EXCEPTION;
user_define_error3 EXCEPTION;
user_define_error4 EXCEPTION;
user_define_error5 EXCEPTION;
user_define_error6 EXCEPTION;
user_define_error7 EXCEPTION;

v_error_msg VARCHAR2(1000);
v_job_start_dm VARCHAR2(14);

BEGIN

    /* 파라미터로 받은 입력값이 적합한지 체크 후, 적합하지 않은 경우 Exception 강제 발생 */
    IF (p_src_system IS NULL OR NOT(p_src_system = 'FMS' OR p_src_system = 'HOMEPAGE')) THEN
        RAISE user_define_error1;
    END IF;
    
    IF (p_src_table IS NULL OR NOT(p_src_table = 'SUPERM' OR p_src_table = 'ACCOUNTM' OR p_src_table = 'TH_MEMBER_MASTER')) THEN
        RAISE user_define_error2;
    END IF;
    
    IF (p_src_cust_id IS NULL) THEN
        RAISE user_define_error3;
    END IF;
    
    IF (p_superm_cust_id IS NULL) THEN
        RAISE user_define_error4;
    END IF;

    IF (p_simularity > 1) THEN
        RAISE user_define_error5;
    END IF;

    IF (p_fix_yn IS NULL OR NOT(p_fix_yn = 'Y' OR p_fix_yn = 'N')) THEN
        RAISE user_define_error6;
    END IF;
    
    IF (p_manual_user_id IS NULL) THEN
        RAISE user_define_error7;
    END IF;

    v_job_start_dm := TO_CHAR(SYSDATE, 'yyyymmddhh24miss');
    
    UPDATE dw_cdd_customer_matching SET
        simularity = p_simularity,
        fix_yn = p_fix_yn,
        rep_superm_cust_yn = p_rep_superm_cust_yn,
        remark = p_remark,
        manual_yn = 'Y',
        manual_reg_user_id = p_manual_user_id,
        manual_upd_user_id = p_manual_user_id,
        manual_reg_dt = TO_CHAR(SYSDATE, 'yyyymmddhh24miss'),
        manual_upd_dt = TO_CHAR(SYSDATE, 'yyyymmddhh24miss')
    WHERE src_system = p_src_system AND
        src_table = p_src_table AND
        src_cust_id = p_src_cust_id AND
        superm_cust_id = p_superm_cust_id;

    COMMIT;        

EXCEPTION
    WHEN user_define_error1 THEN
           RAISE_APPLICATION_ERROR(-20201, '1번째 인자인 원천시스템(p_src_system)은 필수항목이며, 허용되는 값은 "FMS", "HOMEPAGE"만 허용됩니다.');

    WHEN user_define_error2 THEN
           RAISE_APPLICATION_ERROR(-20202, '2번째 인자인 원천고객테이블명(p_src_table)은 필수항목이며, 허용되는 값은 "SUPERM", "ACCOUNTM", "TH_MEMBER_MASTER"만 허용됩니다.');

    WHEN user_define_error3 THEN
           RAISE_APPLICATION_ERROR(-20203, '3번째 인자인 원천고객ID(p_src_cust_id)는 필수항목입니다.');
    
    WHEN user_define_error4 THEN
           RAISE_APPLICATION_ERROR(-20204, '4번째 인자인 매칭후원자번호(p_superm_cust_id)는 필수항목입니다.');

    WHEN user_define_error5 THEN
           RAISE_APPLICATION_ERROR(-20205, '5번째 인자인 유사도(p_simularity)는 최대 1을 넘길 수 없습니다.');

    WHEN user_define_error6 THEN
           RAISE_APPLICATION_ERROR(-20205, '6번째 인자인 고정유무(p_fix_yn)는 필수항목이며, "Y" 또는 "N"만 허용됩니다.');

    WHEN user_define_error7 THEN
           RAISE_APPLICATION_ERROR(-20207, '9번째 인자인 수작업 사용자ID(p_manual_user_id)는 필수항목입니다.');

	WHEN OTHERS THEN
		DBMS_OUTPUT.PUT_LINE(SQLERRM||'ERROR');	
		
		v_error_msg := SQLERRM;
        
        RAISE;

END sp_cdd_xset_match_customer;



/
