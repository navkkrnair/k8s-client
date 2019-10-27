package com.k8s;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

@SpringBootApplication
public class K8sClientApplication
{
	private static final Logger logger = LoggerFactory.getLogger(K8sClientApplication.class);

	public static void main(String[] args)
	{
		SpringApplication.run(K8sClientApplication.class, args);
	}

	@Bean
	CommandLineRunner init()
	{
		return args ->
		{
			KubernetesClient client = new DefaultKubernetesClient();

			// list pods in the default namespace
			logger.info("Listing the pods...");
			PodList pods = client.pods().inNamespace("default").list();
			pods.getItems()
				.stream()
				.forEach(s -> logger.info("Found pod: " + s.getMetadata().getName()));

			// create a pod
			logger.info("Creating a pod");
			Pod pod = client.pods()
				.inNamespace("default")
				.createNew()
				.withNewMetadata()
				.withName("programmatically-created-pod")
				.endMetadata()
				.withNewSpec()
				.addNewContainer()
				.withName("main")
				.withImage("busybox")
				.withCommand(Arrays.asList("sleep", "99999"))
				.endContainer()
				.endSpec()
				.done();
			logger.info("Created pod: " + pod);

			// edit the pod (add a label to it)
			logger.info("edit the pod (add a label to it)");
			client.pods()
				.inNamespace("default")
				.withName("programmatically-created-pod")
				.edit()
				.editMetadata()
				.addToLabels("foo", "bar")
				.endMetadata()
				.done();
			logger.info("Added label foo=bar to pod");
			logger.info("Waiting 1 minute before deleting pod...");
			Thread.sleep(60000);
			// delete the pod
			logger.info("delete the pod");
			client.pods()
				.inNamespace("default")
				.withName("programmatically-created-pod")
				.delete();
			logger.info("Deleted the pod");

		};
	}

}
