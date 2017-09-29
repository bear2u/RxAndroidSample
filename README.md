# RxJava Example

실제 `Android `상에서 많이 쓰고 있으며 유용한 내용들을 골라서 하나씩 적용해보기 위해 진행한다.

1. `Retrofit `활용
2. 캐싱을 이용한 전략
3. `MVP `패턴를 이용한 `Retrofit `활용\(준비중\)
4. 더블 바인딩 \(준비중\)
5. 이벤트 버스\(준비중\)
6. `Multicasting`\(준비중\)
7. 실시간 유효성체크\(준비중\)
8. 초성 검색 \(준비중\)
9. `MVVM `바인딩\(준비중\)

---

# `Retrofit` 활용

작성중..

---

# 캐싱을 이용한 전략

서버를 구축해서 `REST API`를 통해 `Json / Xml` 을 가져오는 일반적인 구조에서 빠른 시간안에 유저에게 보여주는 전략에 대해 고민해본다. 이 내용은 [예제 사이트](https://github.com/kaushikgopal/RxJava-Android-Samples#9-pseudo-caching--retrieve-data-first-from-a-cache-then-a-network-call-using-concat-concateager-merge-or-publish)에서 참고해서 적용했습니다.

총 4가지를 비교를 해봅니다.

1. `Concat `: A \(캐싱\) , B\(네트워크\) 를 조인시 A 먼저 끝나고 B를 실행합니다. 순서를 보장해주지만 A 가 늦게 되는 경우 문제가 발생될수 있다. 
2. `ConcatEager `: B실행시 A를 같이 실행하지만 B를 버퍼링을 보장합니다. 
3. `Merge `: 빨리 나오는 걸 우선으로 보여줍니다. 그래서 순서를 보장하지 않습니다. 만약 캐싱이 늦을 경우 문제가 될 수 있습니다. `SeekBar `를 움직여서 캐싱 시간을 조정해보세요. 그럼 무슨 뜻인지 이해가 되실겁니다.

4. `Publish `:  `publish `에서 `selector`를 사용해서 이 `merge `에 대한 문제를 해결한 걸 볼수 있습니다. 즉 B \(네트워크\) 가 끝날때까지 만약 캐싱이 안 나온 경우 폐기를 하도록 합니다. 

![](https://user-images.githubusercontent.com/9362317/31009787-9a4990d2-a543-11e7-9e58-063039a0b0b8.png)
