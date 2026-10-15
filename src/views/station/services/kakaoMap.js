let pending
export function loadKakaoMaps() {
  if (window.kakao?.maps?.Map) return Promise.resolve(window.kakao.maps)
  if (pending) return pending
  const key = import.meta.env.VITE_KAKAO_MAP_KEY
  if (!key) return Promise.reject(new Error('카카오 지도 키가 설정되지 않았습니다.'))
  pending = new Promise((resolve, reject) => {
    const script = document.createElement('script')
    const timer = setTimeout(() => fail('지도 연결 시간이 초과되었습니다. 네트워크 및 앱 설정을 확인해 주세요.'), 15000)
    function fail(message) { clearTimeout(timer); script.remove(); reject(new Error(message)) }
    script.async = true
    script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=' + encodeURIComponent(key) + '&autoload=false'
    script.onerror = () => fail('카카오 지도를 불러오지 못했습니다. 앱의 지도 사용 설정과 접속 도메인을 확인해 주세요.')
    script.onload = () => {
      if (!window.kakao?.maps?.load) { fail('카카오 지도 SDK를 사용할 수 없습니다.'); return }
      window.kakao.maps.load(() => { clearTimeout(timer); resolve(window.kakao.maps) })
    }
    document.head.appendChild(script)
  }).catch(error => { pending = undefined; throw error })
  return pending
}
export const stationCoordinates = {
  1761: [37.5063,126.6769], 1760: [37.5086,126.7035],
  1759: [37.5085,126.7200], 1758: [37.5068,126.7312],
  1757: [37.5064,126.7422], 1756: [37.5058,126.7532],
  1755: [37.5046,126.7637], 1754: [37.5030,126.7759],
  1753: [37.5037,126.7870], 1752: [37.5054,126.7973],
  1751: [37.5062,126.8109],
}
