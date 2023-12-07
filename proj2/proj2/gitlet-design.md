# Gitlet Design Document

**Name**: Leonard
## .Gitlet Structure

```
1. .gitlet

    1. staging_area
       1. stageState
    2. commits
       1. ...
    3. branch
       1. head
       2. branches
          1. master
          2. ...
    4. blobs
       1. ...
```



## Classes and Data Structures

### Class 1 - Repository 
#### Fields

```
1. File CWD
2. File GITLET_DIR
```

#### method

```
1. void init()
2. void removeFile()
3. void status()
4. File weatherFileExit()
```

### Class 2 - Staging_Area

#### Fields

```
1. File STAGING_AREA_DIR
2. TreeMap<String, String> addition
3. TreeMap<String, String> removal
```

#### method

``` 
1. Staging_Area()
2. void saveStageState()
3. Staging_Area readStageStare()
4. void add()
5. void removeStaging()
6. void remove()
7. TreeMap<String, String> getAddition()
8. TreeMap<String, String> getRemoval()
9. void clearStage()
```

### Class 3 - Commit

#### Fields

``` 
1. File COMMITS_DIR
2. String message
3. List<String> parent
4. String date
5. Map<String, String> blob
```

#### method

``` 
1. commit()
2. commit(String message, String parentSha, Commit oldCommit)
3. String saveCommit()
4. Commit readHeadCommit()
5. boolean sameFileExitInCommit(String fileName, String sha)
6. void makeNewCommit(String message)
7. boolean validMessage(String message)
8. void updateAddition(Map<String, String> addition)
9. void updateRemoval(Map<String, String> removal)
10. void log()
11. void global_log()
12. void find()
13. void reset()
14. void merge()
15. String now()
```

### Class 4 - Branch

#### Fields

``` 
1.File BRANCH_DIR
    1. File HEAD_FILE
    2. File MASTER_FILE
```

#### method
1. ```
   1. branch()
   2. re_branch()
   3. void creatBranch(String name, String sha)
   4. void updateHead(String branchName)
   5. void removeBranch()
   6. void updateBranch(String newCommitSha)
   ```

   
### Class 5 - Blobs

#### Fields
```
1. File BLOBS_DIR
```

#### method
```
1. void creatNewBlob(File file, String name)
```

## Algorithms
### Main:
1. 参数正确性验证
2. 根据参数调用不同功能
3. init
   ```
   1. 参数正确性验证
   2. 调用Repository.init()
   ```
   
4. add
   ```
      1. 初始化验证
      2. 参数正确性验证
      3. 调用Staging_Area.add(String fileName)
   ```
   
5. commit
   ```
      1. 初始化验证
      2. 参数正确性验证
      3. 调用Commit.makeNewCommit(String message)
   ```
   
6. rm
   ```
      1. 初始化验证
      2. 参数正确性验证
      3. 调用Stating_Area.rm(String fileName)
   ```
   
7. log
   ```
      1. 初始化验证
      2. 参数正确性验证
      3. 调用Commit.log()
   ```

8. global_log
   ```
      1. 初始化验证
      2. 参数正确性验证
      3. 调用Commit.global_log()
   ```
   
9. find
   ```
      1. 初始化验证
      2. 参数正确性验证
      3. 调用Commit.find(String message)
   ```
   
10. status
    ```
       1. 初始化验证
       2. 参数正确性验证
       3. 调用Repository.status()
    ```
    
11. checkout
    ```
       1. 初始化验证
       2. 调用Repository.checkout()
    ```
    
12. branch
    ```
       1. 初始化验证
       2. 参数正确性验证
       3. 调用Branch.creatNewBranch()
    ```
    
13. rm_branch
    ```
       1. 初始化验证
       2. 参数正确性验证
       3. 调用Branch.removeBranch()
    ```

14. reset
    ```
       1. 初始化验证
       2. 参数正确性验证
       3. 调用Repository.reset()
    ```
    
15. merge
    ```
       1. 初始化验证
       2. 参数正确性验证
       3. 调用Repository.merge()
    ```

### Repository
1. void init()
   ````
   功能：初始化
   实现：
   ````
   ```
   1. 检查是否初始化，若已经初始化则报错
   2. 创建.gitlet等文件
   3. 创建first Commit
   4. 创建stageState
   5. 调用stageState.saveStageState()，将stageState保存
   6. 调用first.saveCommit(),将first Commit保存,文件名为对应的sha，文件内容为serializable obj,此方法返回commit的sha
   7. 调用Branch.creatBranch(name, sha)创建master
   8. 更新HEAD内容
   ```
   
2. File weatherFileExit(String fileName)
   ````
   功能：查询在CWD目录下是否存在名为fileName的文件，若存在则返回此文件，否则返回null
   实现：
   ````

3. void status()
   ````
   功能：打印.gitlet状态
   实现：
   ````
   ```
   1. 打印Branch部分
      1. 获取所有的branch，获取HEAD指向的branch
      2. 打印branch, 在HEAD指向的branch前加*
   2. 打印Staged Files部分
      1. 读取stageState，遍历stageState.addition打印(排序）
      2. 遍历stageState.removal打印(排序）
   3. 打印Modifications Not Staged For Commit部分
      1. 遍历Commit中的文件，检查在当前文件夹中是否有相同的文件，若无则需打印此文件名(deleted)
         若存在文件名相同但内容不同且不在stageState.additoin中，则需打印此文件名(modified)
      2. 遍历stageState中的文件名，检查stageStage.addition中的文件名是否存在于当前文件夹中，
         若不在当前文件夹中,则需打印(deleted)，若在当前文件夹中，则检查addition中此文件名对应
         的sha是否与当前文件夹的文件相同，若不同则需要打印(modified)
   4. 打印Untracked Files部分
      1. 遍历当前文件夹中的文件名， 查询其是否存在于stageState.addition或stage.removal中，
         若不存在则需打印
   ```

4. void checkout(String args)
   ```
   功能：回退
   实现：
   ```
   ```
   1. 参数正确性验证
   2. 当参数为2(即 -- [file name]情况)
      1. 读取headCommit
      2. 检查fileName是否存在于headCommit中,若不存在则报错并退出
      3. 若存在则将当前文件夹中的文件替换为headCommit中映射的文件
   3. 当参数为3(即 [commit id] -- [file name]情况)
      1. 读取给定的Commit, 若不存在此Commit则报错并退出
      2. 检查fileName是否存在于Commit中,若不存在则报错并推出
      3. 若存在则将当前文件夹中的文件替换为Commit中映射的文件
   4. 当参数为1(即 [branch name]情况)
      1. 如果当前目录下有文件未被跟踪，且checkout命令会重写此文件，则报错并退出
      2. 遍历branch, 如果给定的branch不存在，则报错并退出
      3. 判断给定branch是否与HEAD指向Branch相同，若相同则报错并退出
      4. 删除当前文件所有文件
      5. 将指定Commit中的所有文件读取到当前文件夹
      6. 更新HEAD
      7. 清空stageState
   5. 参数不正确报错退出
   ``` 
   
5. void reset(String commitId)
   ```
   功能：回退文件夹中文件的版本，并移动当前分支的分支头到指定的commit
   实现：
   ```
   ```
   1. 查询是否存在此commitId的commit，若不存在则报错并退出
   2. 查询是否有文件未被跟踪且会被重写，若存在则报错并退出
   3. 删除当前文件夹文件
   4. 将指定Commit中的文件读取到当前文件夹
   5. 移动当前Branch至指定的Commit
   6. 清空stageState, 并保存
   ```
   
6. void merge(String branchName)
   ```
   功能：合并分支
   实现：
   ```
   ```
   1. 读取stageState，如果暂存区不为空则报错退出
   2. 读取给的branch指向的Commit,若此branch不存在则报错并退出
   3. 获取当前HEAD指向的branch, 若与给定branch相同, 则报错并退出
   4. 读取splitCommit、headCommit
   5. 如果有未被跟踪的文件且将会被merge重写或删除，则报错并退出
      1. 如果此文件存在于spiltCommit(不存在于headCommit)
         1. 若branchCommit不存在此文件，Do nothing.
         2. 若branchCommit中存在此文件，且branchCommit中的文件和spiltCommit中的相同
             1. 若此文件和branchCommit中的文件不相同, Do nothing.
             2. 若此文件和branchCommit中的文件相同, Do nothing.
         3. 若branchCommit中存在此文件，且branchCommit中的文件和spiltCommit的不同，报错并退出
      2. 若此文件不存在于splitCommit(不存在于headCommit)
         1. 若branchCommit不存在此文件，Do nothing.
         2. 若branchCommit中存在此文件，此文件与branchCommit中存储的文件相同，Do nothing.
         3. 若branchCommit中存在此文件，此文件与branchCommit中存储的文件不相同，报错并退出
   6. 若splitCommit与branchCommit相同，则报错并退出
   7. 若splitCommit与headBranch相同，则checkout branchCommit并打印message
   8. 创建List<String> visitedFile 保存遍历过的文件
   9. 清空CWD
   10. 遍历splitCommit、headCommit、branchCommit，调用mergeHelper()函数
   11. makeNewComit
   ```
   
7. void mergeHelper(File splitFile, File headFile, File branchFile)
   ```
   功能：实现merge命令的具体行为
   实现：
   ```
   ```
   1. 若spileFile == headFile != branchFile, 保存branchFile，并添加到暂存区
   2. 若splitFile == branchFile != headFile, Do nothing.
   3. 若headFile == branchFile, Do nothing.
   4. 若spiltFile == branchFile == null and headFile != null, Do nothing.
   5. 若splitFile == headFile == null and branchFile != null, 保存branchFile，并添加到暂存区
   6. 若splitFile == headFile != null and branchFile == null, 将headFile添加到暂存删除区
   7. 若splitFile == branchFile != null and headFile == null, Do nothing.
   8. 若headFile != branchFile and headFile != null and branchFile != null, 创建此文件并重写 
   ```
### Staging_Area
1. void add(String fileName)
    ````
   功能：将file添加到暂存添加区
   实现：
   ````
   ```
   1. 遍历Repository.CWD目录文件，查询是否存在此文件
   2. 若不存在则打印出"File dose not exist."，然后退出
   3. 若存在则获取此文件，并且求出此文件的sha1
   4. 调用readStageState(),读取stageState
   5. 查询removal中是否存在fileName,若存在则删除
   6. 调用readHeadCommit(),获取HEAD指向的Commit
   7. 调用sameFileExitInCommit(fileName)检查当前commit中是否存在名为fileName的文件，且sha1相同
   8. 若sameFileExitInCommit()返回值为真，则不做处理
   9. 若sameFileExitInCommit()返回值为假，则创建Blob，并添加<fileName, sha1>的映射 
   ```
2. void saveStageState()
    ```
    功能：保存stageState
    实现：略
    ```
    
3. Staging_Area readStageState()
    ```
    功能：从STAGING_DIR中读取stageState，并返回
    ```
    
4. boolean isStageEmpty()
   ```
   功能：判断addition和removal是否都为空
   ```

5. void rm(String fileName)
   ```
   功能: 将file添加到暂存移出区
   实现：
   ```
   ````
   1. 读取stageState
   2. 读取当前Commit
   3. 查询此文件是否存在于暂存添加区或在当前的Commit被跟踪
   4. 若都为false则报错，并退出
   5. 若文件存在于暂存添加区，则将file的映射从暂存添加区删除，保存stageState
   6. 若文件在当前的Commit中被跟踪，将file的映射添加到暂存删除区,如果此文件还未被删除则删除此文件
   ````
   
### Commit
1. Commit()
   ```
   功能：创建第一个Commit
   实现：略
   ```

2. Commit(String message, String parentSha, Commit oldCommit)
   ```
   功能：创建新的Commit
   ```
   
3. String saveCommit()
   ```
   功能：保存commit对象文件
   实现：
   ```
   ```
   1. 创建tmpFile,调用writeObject()将commit写入tmpFile
   2. 调用readContents()获取commit的byte[] tmpByte
   3. 调用sha1()获取此commit的sha
   4. 调用join(COMMIT_DIR, sha)创建File对象
   5. 调用newCommit.creatNewFile()创建实际对象
   6. 返回此commit的sha
   ```

4. Commit readHeadCommit()
   ```
   功能：读取当前Head指向的Commit
   实现：
   ```
   ````
   1. 获取HEAD当前指向的Commit的Sha1
   2. 遍历COMMITS_DIR，获取HEAD所指向的Commit
   3. 返回此HeadCommit
   ````
5. boolean sameFileExitInCommit(String fileName, String sha)
   ```
   功能：查询commit中是否存在<fileName, sha>的映射
   实现：略
   ```
   
6. String now()
   ```
   功能：返回字符串形式的当前时间
   实现：略
   ```
   
7. void makeNewCommit(String message)
   ````
   功能：提交新的Commit
   实现：
   ````
      ````
      1. 读取stageState
      2. 检查文件的暂存情况，若无文件被暂存，则报错退出
      3. 检查提交的message是否为非空字符串，若为空字符串，则报错退出
      4. 读取当前HEAD所指的Commit
      5. 调用Commit(String message,String parentSha, Commit oldCommit)创建新的Commit
      6. 遍历stageState.addition,stageState.removal 更新newCommit.blob
      7. 清空stageState.addition,stageState.removal
      8. 保存stageState
      9. 保存newCommit
      10. 更新当前Branch指向的Commit
      ````
8. boolean validMessage(String message)
   ```
   功能：message正确性验证
   ```
   
9. void updateAddition(Map<String, String>)
   ```
   功能：遍历stageState.addition,完成更新
   ```
   
10. void updateRemoval(Map<String, String>)
   ```
   功能：遍历stageState.removal,完成更新
   ```

11. void log()
   ```
	功能：打印出headCommit极其之前的提交
	实现：
   ```
   ````
      1. 读取headCommit   
      2. 回溯之前的Commit,按照格式打印信息，若遇到Merge节点选择parent[0](即merge时HEAD所在节点)
      3. 回溯直到初始节点停止
   ````
12. void printLogInfo(String currentCommitSha)
   ```
   功能：打印当前Commit信息
   实现：略
   ```

13. Commit getCommitBySha(String sha)
   ```
   功能：通过给定的sha1获取Commit
   实现：略
   ```

14. void global_log()
   ```
   功能：打印出所有commit的信息
   实现：
   ```
   ```
   1. 通过plainFilenamesIn()获取COMMIT_DIR中Commit的文件名(即Commit的sha)的List
   2. 遍历List,调用getCommitBySha()获取特定的Commit, 调用printLogInfo()打印信息
   ```

15. void find(String message)
   ```
   功能：打印给定message的Commit
   实现：
   ```
   ```
   1. 通过plainFilenamesIn()获取COMMIT_DIR中Commit的文件名(即Commit的sha)的List
   2. 遍历List,调用getCommitBySha()获取特定的Commit,若Commit的message符合则打印出sha
   ```
### Branch
1. void creatBranch(String name, String sha)
    ````
   功能：创建新的Branch,文件名为name,内容为sha
   实现：
   ````
   ````
   1. 创建BRANCH_DIR目录下的file对象，文件名为name
   2. 调用writeContents(file,contents)将sha写入file对象中
   ````
2. void updateHead(Sting branchName)
   ````
   功能：更新HEAD指向的Branch
   ````
3. String headBranch()
    ````
    功能： 返回HEAD指向的提交的sha
    ````

4. void updateBranch(String newCommitSha)
   ````
   功能： 更新当前HEAD指向的Branch,使其指向新的提交
   ````

5. void creatNewBranch(String newBranchName)
   ```
   功能：创建新的分支并指向HEAD指向的Commit
   实现：
   ```
   ```
   1. 遍历BRANCHED_DIR，判断newBranchName是否已经存在，若存在则报错
   2. 获取headCommit的sha
   3. 根据newBranchName和headCommitSha创建新的Branch
   ```
6. void removeBranch(String branchName)
   ```
   功能：移除Branch
   实现：
   ```
   ```
   1. 遍历Branch,若无名为branchName的Branch则报错退出
   3. 获取当前HEAD指向的Branch，若与要删除的branch相同则报错并退出
   2. 获取此branch，删除
   ```
   

### Blobs
1. void createNewBlob(File file, String sha)
   ```
   功能：创建新的Blob,文件名为sha,内容为file的byte[]
   实现：略
   ```

## Persistence

### Repository
1. File weatherFileExit(String fileName)
   ```
   功能：查询在CWD目录下是否存在名为fileName的文件，若存在则返回此文件，否则返回null
   ```

### Staging_Area
1. void saveStageState()
   ```
   功能：保存stageState
   ```
2. Staging_Area readStageState()
   ```
   功能：从STAGING_DIR目录下读取stageState，并返回
   ```

### Commit
1. Commit readHeadCommit()
   ```
   功能：读取当前HEAD指向的Commit
   ```
   
2. boolean samFileExitInCommit(String fileName, String sha)
   ```
   功能：查询commit中是否存在<fileName, sha>的映射
   ```

### Branch
1. void creatBranch(String name, String sha)
   ```
   功能：创建新的Branch,文件名为name,内容为sha
   ```
2. String headBranch()
   ```
   功能：返回HEAD储存的sha
   ```


### Blobs
1. void createNewBlob(File file, String sha)
   ```
   功能：创建新的Blob，文件名为sha，内容为file的byte[]
   ```
   
   