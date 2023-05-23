package org.example.model;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Data
public class GetStatus {
	@NotNull
	@Min(0)
	Long summ;
	ArrayList<?> fromTransaction;
	ArrayList<?> toTransaction;
}
