import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelAlgorithm {

    public static void main(String[] args) {
        int[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int result = parallelSum(data);
        System.out.println("The sum is: " + result);
    }

    public static int parallelSum(int[] data) {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(new SumTask(data, 0, data.length));
    }

    private static class SumTask extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 4;
        private int[] data;
        private int start;
        private int end;

        public SumTask(int[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            int length = end - start;
            if (length <= THRESHOLD) {
                int sum = 0;
                for (int i = start; i < end; i++) {
                    sum += data[i];
                }
                return sum;
            } else {
                int midpoint = start + length / 2;
                SumTask leftTask = new SumTask(data, start, midpoint);
                SumTask rightTask = new SumTask(data, midpoint, end);
                leftTask.fork();
                int rightResult = rightTask.compute();
                int leftResult = leftTask.join();
                return leftResult + rightResult;
            }
        }
    }
}
Trong ví dụ này, chúng ta sử dụng lớp ForkJoinPool để quản lý các luồng và tạo các tác vụ đệ quy song song để tính tổng các số trong một mảng. Lớp SumTask kế thừa RecursiveTask<Integer> và tính toán tổng của một phần của mảng bằng cách chia nó thành hai phần và tạo hai tác vụ con để tính toán chúng. Các tác vụ con này được tạo bằng cách sử dụng từ khóa fork() và tính toán bằng cách sử dụng từ khóa join(). Khi số lượng phần tử trong mảng là nhỏ hơn hoặc bằng một ngưỡng được chỉ định, chúng ta sử dụng phương thức compute() của lớp SumTask để tính toán tổng trên một luồng duy nhất.