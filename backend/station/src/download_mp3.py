import os
import time
import requests
from playwright.sync_api import sync_playwright

def download_youtube_audio(url):
    save_directory = "./saved_mp3"
    os.makedirs(save_directory, exist_ok=True)

    with sync_playwright() as p:
        try:
            total_start_time = time.time()

            # 브라우저 실행
            step_start_time = time.time()
            browser = p.chromium.launch(headless=True)
            context = browser.new_context()
            print(f"브라우저 실행: {time.time() - step_start_time:.2f}초 소요")

            # 페이지 열기
            step_start_time = time.time()
            page = context.new_page()

            # 속도 빠르게 하기 위해 애니메이션 등 비활성화
            page.add_init_script("""
                document.querySelectorAll('*').forEach(el => {
                    el.style.transition = 'none';
                    el.style.animation = 'none';
                });
            """)

            print("YouTube 다운로드 사이트 열기")
            page.goto("https://yt5s.biz/enxj100/", wait_until="domcontentloaded")
            print(f"페이지 열기 완료: {time.time() - step_start_time:.2f}초 소요")

            # URL 입력 및 제출
            step_start_time = time.time()
            print(f"YouTube URL 입력: {url}")
            page.fill("#txt-url", url)
            page.click("#btn-submit")
            print(f"URL 제출 완료: {time.time() - step_start_time:.2f}초 소요")

            # MP3 버튼 대기 및 클릭
            step_start_time = time.time()
            print("MP3 128k 버튼 대기")
            mp3_button = page.locator("button[data-ftype='mp3'][data-fquality='128']")
            mp3_button.wait_for(state="visible", timeout=10_000)  # 대기 시간 축소
            mp3_button.click()
            print(f"MP3 버튼 클릭 완료: {time.time() - step_start_time:.2f}초 소요")

            # 모달 대기 및 다운로드 URL 추출
            step_start_time = time.time()
            print("모달 대기 중")
            page.wait_for_selector("a#A_downloadUrl", timeout=10_000)  # 대기 시간 축소
            download_anchor = page.locator("a#A_downloadUrl")
            download_url = download_anchor.evaluate("anchor => anchor.href")
            print(f"다운로드 URL 추출 완료: {time.time() - step_start_time:.2f}초 소요")

            if not download_url:
                print("다운로드 URL 찾을 수 없음. 디버깅 중")
                print(page.content())  # 디버깅용
                page.screenshot(path="debug_modal.png")
                return

            print(f"다운로드 URL: {download_url}")

            # MP3 파일 다운로드
            step_start_time = time.time()
            file_name = f"{download_url.split('/')[-1].split('?')[0]}.mp3"
            save_path = os.path.join(save_directory, file_name)
            print(f"MP3 파일 다운로드 위치: {save_path}")
            response = requests.get(download_url, stream=True)
            response.raise_for_status()
            with open(save_path, "wb") as f:
                for chunk in response.iter_content(chunk_size=262144):  # 256KB 단위
                    f.write(chunk)
            print(f"MP3 파일 다운로드 완료: {time.time() - step_start_time:.2f}초 소요")

            # 전체 실행 시간
            print(f"전체 실행 시간: {time.time() - total_start_time:.2f}초 소요")

        except Exception as e:
            print(f"오류 발생: {e}")
        finally:
            browser.close()

# 유튜브 URL
youtube_url = "https://www.youtube.com/watch?v=ekr2nIex040"
download_youtube_audio(youtube_url)
