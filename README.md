# 开发
需要依次为 lib/ 下的 jar 包执行右键、Make Directory as...


## Bad Case
```
// 包含 extends 的复杂类型
final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
```