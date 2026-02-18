# TastingGenie 近代化監査メモ（Play ストア公開を見据えた最小現実路線）

この文書は、現行コードを読み取ったうえで「今の Android で動く状態」＋「公開品質に近づける」ための、段階的な改修案です。

## 1. 現状の主要課題（コード起点）

### 1-1. ビルド基盤が古く、現行 Android SDK 要件に追従できていない
- Android Gradle Plugin が `3.5.1`、Kotlin が `1.3.11`、依存レポジトリに `jcenter()` を利用しており、現行開発環境との相性が悪いです。
- `compileSdkVersion` / `targetSdkVersion` が `28` で止まっています。
- AndroidX ではなく旧 Support Library（`com.android.support:*`）を使っています。

**影響**
- 新しい Android Studio / SDK でビルド不能または不安定。
- Play ストアの targetSdk 要件を満たせない。

### 1-2. 画像保存方式が重く、将来互換性も低い
- 画像を `MediaStore.MediaColumns.DATA` から生ファイルパスへ変換しており、スコープドストレージ時代には壊れやすい実装です。
- 「圧縮版＋非圧縮版（uncompressed）」の 2 重保存があり、容量増加を招きます。
- `MediaStore.Images.Media.getBitmap(...)` は非推奨系 API です。

**影響**
- 端末容量を圧迫、アプリ体感劣化。
- Android 10+ での互換性リスク。

### 1-3. 権限・ストレージ設計が古い
- `WRITE_EXTERNAL_STORAGE` を要求しています。
- カメラ撮影〜画像選択処理が `startActivityForResult` ベースです。

**影響**
- 新しい権限モデルへの非適合。
- 将来的なメンテコスト増。

### 1-4. アプリ構造が密結合で、品質改善しにくい
- `MainActivity` が DB 操作、画面遷移、画像処理ロジックを広く直接担当しています。
- Fragment 側でも View 参照・バリデーション・状態管理が分散し、見通しが悪いです。

**影響**
- 修正時の副作用が読みにくい。
- テスト導入や機能追加の難易度が高い。

### 1-5. DB 層が Anko 依存、移行しづらい
- DB アクセスに `anko-sqlite` を使用。
- マイグレーションが手作業 SQL 中心。

**影響**
- ライブラリ寿命・保守性の問題。
- スキーマ変更の安全性確保が難しい。

### 1-6. 明確なテスト基盤がほぼない
- `testImplementation 'junit:junit:4.12'` はあるが、実テストコードは見当たりません。

**影響**
- 仕様退行を防げない。
- 改修のたびに手動確認コストが増大。

### 1-7. 実害バグ候補
- `SharedPreferenceManager` の setter が `edit().putBoolean(...)` 後に `apply()` / `commit()` を呼んでおらず、値保存されません。

**影響**
- 「非圧縮画像保存の確認」などの設定が反映されない可能性。

---

## 2. 目標に対する現実的な改修ステップ（気負いすぎない版）

### Phase 0（最優先・公開最低ライン）
1. **ビルドを現行化**
   - AGP / Gradle / Kotlin を段階更新（いきなり最新固定ではなく 2〜3段階移行）。
   - `jcenter()` を除去し `google()` + `mavenCentral()` へ。
   - AndroidX へ移行（Jetifier は暫定で ON）。
2. **targetSdk 更新**
   - まず `compileSdk/targetSdk` を現行要件まで上げる。
3. **壊れやすい API を置換**
   - `startActivityForResult` -> Activity Result API。
   - 旧ストレージ権限依存を見直し（Photo Picker / SAF 優先）。
4. **明確バグ修正**
   - SharedPreferences の `apply()` 漏れを修正。

**完了条件（最小）**
- Debug/Release ビルド可能。
- 主要導線（一覧→登録→レビュー→画像追加）がクラッシュしない。

### Phase 1（体感品質を上げる）
1. **画像パイプライン改善**
   - 原則 1 ファイル保存（非圧縮複製をやめる）。
   - 必要サイズに合わせたサムネイル生成＋表示（Coil/Glide 導入）。
   - メタ情報は DB（または URI）で管理し、実ファイルの責務を明確化。
2. **ストレージ責務分離**
   - `ImageRepository` を作り、Fragment/Activity からファイル I/O を追い出す。
3. **クラッシュ予防**
   - `context!!` 連発箇所を安全化（`requireContext` / `viewLifecycleOwner` など）。

**完了条件**
- 画像付きデータ 100 件規模で操作遅延が体感改善。
- 画像まわりの不具合再現率低下。

### Phase 2（保守性・公開品質）
1. **DB を Room 化（段階移行）**
   - 既存 SQLite から Room エンティティへ写像。
   - Migration をテストで担保。
2. **UI 層整理**
   - ViewBinding 導入（Kotlin synthetic 廃止）。
   - 可能なら ViewModel + Repository へ分離。
3. **リリース品質整備**
   - Crashlytics / Analytics（必要最小限）
   - ProGuard/R8, baseline profile, app size 定点観測。

---

## 3. テスト戦略（まずは最小セット）

### 3-1. すぐ入れるべきテスト
- **Unit Test**
  - `degToSweetLevel` の境界値テスト。
  - 入力変換（`viewToInt`, `viewToFloat` 相当）の異常値テスト。
- **Instrumentation / UI Test（最小）**
  - 「酒情報の新規登録」
  - 「レビュー追加」
  - 「画像選択（モック URI）」

### 3-2. 目標
- 重要導線に対して「壊れたら CI で赤になる」状態を先に作る。
- 網羅率より、**事故りやすい箇所の防波堤**を優先。

---

## 4. Play ストア公開に向けたチェックリスト（最低限）

- targetSdk を最新要件に追従。
- 64-bit / 署名 / versionCode 運用。
- プライバシーポリシー（カメラ・画像扱いの説明）。
- クラッシュ率監視（公開後すぐに確認可能な体制）。
- 主要端末（Pixel + 国内ミドル帯 1 台程度）で動作確認。

---

## 5. 直近 2〜4 週間の実行案（現実的スコープ）

1週目:
- ビルド基盤更新（AGP/Kotlin/AndroidX/SDK）
- SharedPreferences バグ修正

2週目:
- 画像入出力を repository 化、2重保存停止
- 画像選択 API を Activity Result へ

3週目:
- 主要導線テスト（unit + 最小 UI）
- 不具合修正サイクル

4週目:
- リリースビルド検証
- ストア公開素材/ポリシー準備

---

## 6. 補足（進め方のコツ）

- **一度に完璧を狙わない**: 先に「動く・落ちない・公開できる」を固める。
- **見た目改修より土台優先**: 今回は build/storage/test の順が費用対効果高。
- **破壊的変更は段階的に**: AndroidX 移行と Room 化を同時にやらない。

