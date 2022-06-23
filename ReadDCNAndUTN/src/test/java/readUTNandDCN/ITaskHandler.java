package readUTNandDCN;

public interface ITaskHandler<T> {
	void perform(T work, int currentInput) throws Exception;
}
