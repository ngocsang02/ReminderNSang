package com.theartofdev.edmodo.cropper.ml;

import android.content.Context;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.CastOp;
import org.tensorflow.lite.support.common.ops.DequantizeOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.common.ops.QuantizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.metadata.MetadataExtractor;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public final class Detectposter {
  @NonNull
  private final ImageProcessor imageProcessor;

  private int imageHeight;

  private int imageWidth;

  @NonNull
  private final List<String> tempMeta;

  @NonNull
  private final TensorProcessor outputPostProcessor;

  @NonNull
  private final Model model;

  private Detectposter(@NonNull Context context, @NonNull Model.Options options) throws
      IOException {
    model = Model.createModel(context, "detectposter.tflite", options);
    MetadataExtractor extractor = new MetadataExtractor(model.getData());
    ImageProcessor.Builder imageProcessorBuilder = new ImageProcessor.Builder()
      .add(new ResizeOp(640, 640, ResizeMethod.NEAREST_NEIGHBOR))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}))
      .add(new QuantizeOp(0f, 0.0f))
      .add(new CastOp(DataType.FLOAT32));
    imageProcessor = imageProcessorBuilder.build();
    TensorProcessor.Builder outputPostProcessorBuilder = new TensorProcessor.Builder()
      .add(new DequantizeOp((float)0, (float)0.0))
      .add(new NormalizeOp(new float[] {0.0f}, new float[] {1.0f}));
    outputPostProcessor = outputPostProcessorBuilder.build();
    tempMeta = FileUtil.loadLabels(extractor.getAssociatedFile("temp_meta.txt"));
  }

  @NonNull
  public static Detectposter newInstance(@NonNull Context context) throws IOException {
    return new Detectposter(context, (new Model.Options.Builder()).build());
  }

  @NonNull
  public static Detectposter newInstance(@NonNull Context context, @NonNull Model.Options options)
      throws IOException {
    return new Detectposter(context, options);
  }

  @NonNull
  public Outputs process(@NonNull TensorImage image) {
    imageHeight = image.getHeight();
    imageWidth = image.getWidth();
    TensorImage processedimage = imageProcessor.process(image);
    Outputs outputs = new Outputs(model);
    model.run(new Object[] {processedimage.getBuffer()}, outputs.getBuffer());
    return outputs;
  }

  public void close() {
    model.close();
  }

  @NonNull
  public Outputs process(@NonNull TensorBuffer image) {
    TensorBuffer processedimage = image;
    Outputs outputs = new Outputs(model);
    model.run(new Object[] {processedimage.getBuffer()}, outputs.getBuffer());
    return outputs;
  }

  public class Outputs {
    private TensorBuffer output;

    private Outputs(Model model) {
      this.output = TensorBuffer.createFixedSize(model.getOutputTensorShape(0), DataType.FLOAT32);
    }

    @NonNull
    public List<Category> getOutputAsCategoryList() {
      return new TensorLabel(tempMeta, outputPostProcessor.process(output)).getCategoryList();
    }

    @NonNull
    public TensorBuffer getOutputAsTensorBuffer() {
      return outputPostProcessor.process(output);
    }

    @NonNull
    private Map<Integer, Object> getBuffer() {
      Map<Integer, Object> outputs = new HashMap<>();
      outputs.put(0, output.getBuffer());
      return outputs;
    }
  }
}
