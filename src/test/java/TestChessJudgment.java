import org.junit.Test;

/**
 * @author lwz
 * @create 2021-12-13 13:22
 * @description:
 */
public class TestChessJudgment {
    @Test
    public void test01() {
        int[][] array = {
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        int x = 0;
        int y = 0;
        boolean b = checkWin(x, y, array);
        System.out.println(b);
    }

    @Test
    public void test02() {
        int[][] array = {{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
        int x = 9;
        int y = 0;
        boolean b = checkResult(x, y, 1, array);
        System.out.println(b);
    }

    //判断输赢
    public boolean checkWin(int xIndex, int yIndex, int[][] chessArray) {
        int count;
        boolean flag;
        int max = 0;
        int tempXIndex = xIndex;
        int tempYIndex = yIndex;

        // 三维数组记录横向，纵向，左斜，右斜的移动
        int[][][] dir = new int[][][]{
                // 横向
                {{-1, 0}, {1, 0}},
                // 竖着
                {{0, -1}, {0, 1}},
                // 左斜
                {{-1, -1}, {1, 1}},
                // 右斜
                {{1, -1}, {-1, 1}}};
        for (int i = 0; i < 4; i++) {
            count = 1;
            //j为0,1分别为棋子的两边方向，比如对于横向的时候，j=0,表示下棋位子的左边，j=1的时候表示右边
            for (int j = 0; j < 2; j++) {
                flag = true;
                /**
                 while语句中为一直向某一个方向遍历
                 有相同颜色的棋子的时候，Count++
                 否则置flag为false，结束该该方向的遍历
                 **/
                while (flag) {
                    tempXIndex = tempXIndex + dir[i][j][0];
                    tempYIndex = tempYIndex + dir[i][j][1];

                    //这里加上棋盘大小的判断，这里我设置的棋盘大小为16 具体可根据实际情况设置 防止越界
                    if (tempXIndex >= 0 && tempXIndex < 16 && tempYIndex >= 0 && tempYIndex < 16) {
                        if ((chessArray[tempXIndex][tempYIndex] == chessArray[xIndex][yIndex])) {
                            count++;
                        } else
                            flag = false;
                    } else {
                        flag = false;
                    }
                }
                tempXIndex = xIndex;
                tempYIndex = yIndex;
            }

            if (count >= 5) {
                max = 1;
                break;
            } else
                max = 0;
        }
        if (max == 1)
            return true;
        else
            return false;
    }

    public boolean checkResult(int x, int y, int color, int[][] chessboardArr) {
        boolean isSame;
        // 判断水平方向
        for (int i = 4; i >= 0; i--) {
            isSame = true;
            for (int j = 0; j < 5; j++) {
                int thisX = x - i + j;
                if (isOverChessboard(thisX, y)) {
                    isSame = false;
                    break;
                }
                if (chessboardArr[thisX][y] != color) {
                    isSame = false;
                    break;                // 一旦发现此处不符合条件，强制退出当前循环
                }
            }
            if (isSame) {
                return true;
            }
        }
        // 判断垂直方向
        for (int i = 4; i >= 0; i--) {
            isSame = true;
            for (int j = 0; j < 5; j++) {
                int thisY = y - i + j;
                if (thisY < 0 || thisY > 14) {
                    isSame = false;
                    break;
                }
                if (chessboardArr[x][thisY] != color) {
                    isSame = false;
                    break;                // 一旦发现此处不符合条件，强制退出当前循环
                }
            }
            if (isSame) {
                return true;
            }
        }
        // 判断斜上方向
        for (int i = 4; i >= 0; i--) {
            isSame = true;
            for (int j = 0; j < 5; j++) {
                int thisX = x - i + j;
                int thisY = y + i - j;
                if (thisX < 0 || thisY < 0) {
                    isSame = false;
                    break;
                }
                if (chessboardArr[thisX][thisY] != color) {
                    isSame = false;
                    break;                // 一旦发现此处不符合条件，强制退出当前循环
                }
            }
            if (isSame) {
                return true;
            }
        }
        // 判断斜下方向
        for (int i = 4; i >= 0; i--) {
            isSame = true;
            for (int j = 0; j < 5; j++) {
                int thisX = x - i + j;
                int thisY = y - i + j;
                if (thisX < 0 || thisY < 0) {
                    isSame = false;
                    break;
                }
                if (chessboardArr[thisX][thisY] != color) {
                    isSame = false;
                    break;                // 一旦发现此处不符合条件，强制退出当前循环
                }
            }
            if (isSame) {
                return true;
            }
        }
        return false;
    }

    /**
     * 固定为16*16的棋盘
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isOverChessboard(int x, int y) {
        return x < 0 || y < 0 || x > 16 || y > 16;
    }


}
