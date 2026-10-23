# 데모 첨부파일 적용

`mini404_통합_데모데이터.sql`의 마지막 정리 구간은 공지사항, 자료실, Q&A 일부에 이 폴더의 파일을 연결합니다. SQL에는 파일 정보가 들어가지만 파일 바이트는 PostgreSQL이 아닌 백엔드 업로드 볼륨에 저장됩니다.

1. 중앙 DB의 `mini404`에 통합 SQL 파일 전체를 실행합니다.
2. 중앙 백엔드 컨테이너가 실행 중인 서버에서 PowerShell을 열고 아래 명령을 실행합니다.

```powershell
docker cp "C:\Users\ez\Desktop\mini\mini-데이터베이스-SQL\demo-uploads\demo_facility_guide.txt" mini-1-backend:/app/uploads/demo_facility_guide.txt
docker cp "C:\Users\ez\Desktop\mini\mini-데이터베이스-SQL\demo-uploads\demo_transfer_guide.txt" mini-1-backend:/app/uploads/demo_transfer_guide.txt
docker cp "C:\Users\ez\Desktop\mini\mini-데이터베이스-SQL\demo-uploads\demo_qna_reference.txt" mini-1-backend:/app/uploads/demo_qna_reference.txt
```

Compose의 `uploaded_files` 볼륨에 복사되므로 중앙 백엔드가 계속 실행되는 동안 모든 사이트 사용자가 같은 파일을 다운로드할 수 있습니다. SQL을 다시 실행하면 첨부파일 메타데이터도 초기화되므로 위 복사 명령도 다시 실행해야 합니다.

각 파일은 최대 **20 MB**, 한 번의 업로드 요청은 파일들을 합쳐 최대 **60 MB**입니다. 여러 파일을 올릴 때는 요청 전체 크기가 60 MB를 넘지 않도록 해주세요.
