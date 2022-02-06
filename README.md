# Mobile-App-Dev

![aaaaaa](https://imgs.xkcd.com/comics/git.png)

### Useful note and links I found in the internet

*Put note or links you found in the internet that may be useful for our project here*

[Writing on GitHub](https://help.github.com/en/github/writing-on-github/basic-writing-and-formatting-syntax)  
[List of common Git Command](https://github.com/joshnh/Git-Commands)  
[Learn Git and GitHub YT link](https://www.youtube.com/playlist?list=PLriKzYyLb28nCh3jJLROcYBvj7ZO0l-3G)  


## Git guide

**Tips and PSA**
- When in doubt, `git status` and `git help`
- Please use comment as much as possible to explain why you write such a code
- Please specify what you have changed in git commit message. Do not just say "change few stuff here and there" in your commit message

**One time only**  
Cloning
1. Open the folder that you use for your project
2. Right click anywhere on the folder and click open Git Bash here
3. Type this command: `https://github.com/ikmalk/Mobile-App-Dev.git`
4. You may be asked to login to your github

Set up email and name (This will appear when you do a commit)
1. Open the folder that you use for your project
2. Right click anywhere on the folder and click open Git Bash here
3. Set your username by typing: `git config --global user.name "YourName"`
4. Set your email by typing: `git config --global user.email "YourEmail@email.com"`

**Commit and push**

- **Committing to master branch**
1. Open the folder of your project and open git bash
2. Type: `git status`
3. See the file name you have not added / have removed
4. Type: `git add/[rm -r] filename` //add for `add` and `rm` for remove
5. Type: `git commit -m "Your commit message here"`
6. Type: `git push` to send it online in the github repository.

- **Comitting to your branch**
1. Open project folder and open git bash.
2. Type: `git branch -a` to see all the branches in repo.
3. Type: `git checkout branchname` to switch to your branch. You should see the change from (master) --> (branchname)
4. Follow step 2 - 6 in comitting master branch. Your git bash will push to your branch unless specified.
5. Extra step. You can push to an existing branch by typing `git push origin branchname`

**Fetch and pulling**  
*Will Update Later*  
1. Open the folder of your project and open git bash
2. Type: `git status` 
3. Type: `git pull`
