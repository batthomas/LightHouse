package com.illuminai.vision.backend.render;

import com.illuminai.vision.backend.math.Matrix3x3;
import com.illuminai.vision.backend.math.Point3d;
import com.illuminai.vision.backend.math.Vector3d;
import com.illuminai.vision.backend.scene.Scene;
import com.illuminai.vision.backend.scene.shape.Shape;
import com.illuminai.vision.frontend.Screen;

public class Raytracer {

    private Scene scene;

    private Point3d position;

    private Point3d rotation;

    public Raytracer(Scene scene) {
        this.scene = scene;
        position = new Point3d(-5,0,0);
        rotation = new Point3d(0,0,0);
    }

    public Screen renderScene() {
        Screen screen = new Screen(500,500);
        Matrix3x3 rotationMatrix = Matrix3x3.createRotationMatrix(rotation.getX(), rotation.getY(), rotation.getZ());
        for (int x = 0; x < 500; x++) {
            for (int y = 0; y < 500; y++) {
                double u = (x - 250) / 500.0;
                double v = (y - 250) / 500.0;
                Point3d origin = new Point3d(position.getX(),position.getY(),position.getZ());
                Vector3d direction = rotationMatrix.transformed(new Vector3d(10,0,0)
                        .add(new Vector3d(0,0,-4).scale(v))
                        .add(new Vector3d(0,4,0).scale(u)));

                Ray ray = new Ray(origin, direction);

                Intersection nearest = null;
                for (Shape shape : scene.getShapes()) {
                    Intersection intersection = shape.getIntersection(ray);
                    if (nearest == null || (intersection != null && intersection.getTime() < nearest.getTime())) {
                        nearest = intersection;
                    }
                }

                if (nearest != null) {
                    screen.setPixel(x,y, nearest.getShape().getColor());
                } else {
                    screen.setPixel(x, y, 0xff00ff);
                }
            }
        }
        return screen;
    }

    /** Returns the direction which currently looked at*/
    public Vector3d getDirection() {
        return Matrix3x3.createRotationMatrix(rotation.getX(), rotation.getY(), rotation.getZ()).transformed(new Vector3d(1,0,0));
    }

    /** Moves the camera forward in the direction of {@link #rotation}*/
    public void moveForward(double amount) {
        position.set(position.add(getDirection().scale(amount)));
    }

    /** Moves the camera forward in the direction perpendicular to {@link #rotation}*/
    public void moveSideward(double amount) {
        position.set(position.add(Matrix3x3.createRotationMatrix('z',Math.PI/2).transformed(getDirection().scale(amount))));
    }

    public void moveUpwards(double amount) {
        position.set(position.add(new Vector3d(0,0,1).scale(amount)));
    }

    public Scene getScene() {
        return scene;
    }

    public Point3d getPosition() {
        return position;
    }

    public void setPosition(Point3d position) {
        this.position = position;
    }

    public Point3d getRotation() {
        return rotation;
    }

    public void setRotation(Point3d rotation) {
        this.rotation = rotation;
    }
}
